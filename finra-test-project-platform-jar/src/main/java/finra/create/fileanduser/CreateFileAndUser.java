package finra.create.fileanduser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import finra.base.webscript.BaseWebScript;
import finra.common.exception.FinraException;
import finra.common.helper.HelperWebScript;
import finra.common.impl.FinraFileCreator;
import finra.common.impl.FinraNodeCreator;
import finra.common.impl.FinraUserCreator;

/**
 * A Java controller to create file and user.
 *
 * @author https://www.linkedin.com/in/sanjaytaunk/
 * @Created Nov/25/2019
 */
@Configuration
@ComponentScan("finra.common.impl")
@Component("CreateFileAndUser")
public class CreateFileAndUser extends BaseWebScript {

	public static Logger log = LoggerFactory.getLogger(CreateFileAndUser.class);

	protected Repository repositoryHelper;

	@Autowired
	@Qualifier(value = "FinraFileCreator")
	FinraFileCreator finraFileCreator;

	@Autowired
	@Qualifier(value = "FinraUserCreator")
	FinraUserCreator finraUserCreator;

	@Autowired
	@Qualifier(value = "FinraNodeCreator")
	public FinraNodeCreator finraNodeCreator;

	@Override
	public void executeService(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) {

		FormField content = null;
		try {
			Map<String, String> urlParameters = getUrlParameters(webScriptRequest);
			final String fileName = urlParameters.get("fileName");
			String userId = urlParameters.get("userId");

			WebScriptServletRequest webScriptServletRequest = (WebScriptServletRequest) webScriptRequest;
			content = webScriptServletRequest.getFileField("content");

			log.debug("Create " + fileName + " request received.");
			if (content == null) {
				throw new FinraException.FinraWebScriptException(HttpStatus.BAD_REQUEST,
						"Please verify 'content' parameter, it should NOT be empty.");
			}

			log.debug("### Executing " + Thread.currentThread().getStackTrace()[1].getMethodName() + " ####");
			log.debug("### Creating : " + fileName + " and " + userId + " ");

			NodeRef caseFolderNodeRef = finraNodeCreator.createOrGetFinraFolder();

			final NodeRef parentFolder = caseFolderNodeRef;
			final FormField finalContent = content;
			NodeRef fileNodeRef = finraFileCreator.createFile(fileName, parentFolder, finalContent);
			NodeRef personNodeRef = finraUserCreator.createUser();

			Map<String, String> hm = new HashMap<String, String>();
			hm.put(fileName, fileNodeRef.toString());
			hm.put(userId, personNodeRef.toString());

			// Construct and Send Response
			webScriptResponse.setStatus(Status.STATUS_CREATED);
			webScriptResponse.setContentType("application/json");
			if (webScriptResponse.getOutputStream() != null) {
				webScriptResponse.getOutputStream().write(HelperWebScript.toJson(hm));
				contentCleanup(content);
			}

		} catch (IOException e) {
			contentCleanup(content);
			throw new FinraException.FinraWebScriptException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
		}
	}
}