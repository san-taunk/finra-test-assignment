package finra.grant.readpermissiontouser;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.http.HttpStatus;

import finra.base.webscript.BaseWebScript;
import finra.common.exception.FinraException;
import finra.common.helper.HelperWebScript;
import finra.common.impl.FinraGrantReadAccessToUser;
import finra.dto.GrantReadAccessDto;
/**
 * A Java controller to grant read access to user on noderef.
 *
 * @author https://www.linkedin.com/in/sanjaytaunk/
 * @Created Nov/25/2019
 */
public class GrantReadPermission extends BaseWebScript {
    public static Logger log = LoggerFactory.getLogger(GrantReadPermission.class);
    
	@Autowired
	@Qualifier(value = "FinraGrantReadAccessToUser")
	FinraGrantReadAccessToUser finraGrantReadAccessToUser;

	@Override
	public void executeService(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse){
		log.debug("### Executing " + Thread.currentThread().getStackTrace()[1].getMethodName() + " ####");
		try {
			String metadata = webScriptRequest.getParameter("metadata");
            GrantReadAccessDto lclGrantReadAccessDto = null;
            try {
            	lclGrantReadAccessDto = HelperWebScript.unMarshall(metadata, GrantReadAccessDto.class);
            } catch (IOException e) {
                throw new FinraException.FinraWebScriptException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
            }
			final GrantReadAccessDto finalGrantReadAccessDto = lclGrantReadAccessDto;
			finraGrantReadAccessToUser.grantReadAccessToUser(finalGrantReadAccessDto);			
			
			if (lclGrantReadAccessDto != null) {
				webScriptResponse.setStatus(Status.STATUS_OK);
				webScriptResponse.setContentType("application/json");
				if (webScriptResponse.getOutputStream() != null) {
					webScriptResponse.getOutputStream().write(HelperWebScript.toJson(lclGrantReadAccessDto));
				}
			} else {
				throw new FinraException.FinraWebScriptException(HttpStatus.BAD_REQUEST, "Granting READ access request failed.");
			}
		} catch (IOException e) {
			throw new FinraException.FinraWebScriptException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
		}
		
	}    
}