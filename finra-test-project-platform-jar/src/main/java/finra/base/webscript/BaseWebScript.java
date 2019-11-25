package finra.base.webscript;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;
import org.springframework.http.HttpStatus;

import finra.common.exception.FinraException;

public abstract class BaseWebScript extends AbstractWebScript {
	public static Logger log = LoggerFactory.getLogger(BaseWebScript.class);

	protected abstract void executeService(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse);

	@Override
	public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) throws IOException {

		try {
			executeService(webScriptRequest, webScriptResponse);
		} catch (Exception e) {
			log.debug(e.getMessage() + e.getStackTrace());
		}

	}

	protected void contentCleanup(FormField content) {
		if (content != null && content.getIsFile()) {
			content.cleanup();
		}
	}

	public static Map<String, String> getUrlParameters(WebScriptRequest req) {
		if (req.getServiceMatch() == null) {
			throw new FinraException.FinraWebScriptException(HttpStatus.BAD_REQUEST,
					"The matching API Service for the request is null.");
		}

		Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
		if (templateVars == null) {
			throw new FinraException.FinraWebScriptException(HttpStatus.BAD_REQUEST,
					"The template variable substitutions map is null");
		}

		return templateVars;
	}

}