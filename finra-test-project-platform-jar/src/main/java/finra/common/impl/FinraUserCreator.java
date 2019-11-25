package finra.common.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationContext;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import finra.common.exception.FinraException;

@Component("FinraUserCreator")
public class FinraUserCreator {

	public static Logger log = LoggerFactory.getLogger(FinraUserCreator.class);
	
	@Autowired
	PersonService personService;
	@Autowired
	MutableAuthenticationService authenticationService;
	@Autowired
    private AuthenticationContext authenticationContext;
	
	@Autowired
	@Qualifier("TransactionService")
	public TransactionService transactionService;	

	public NodeRef createUser() {

		NodeRef personNodeRef = null;
		String userName = "finraUserOne";
		String userPassword = "testPassword123";

		if (userName != null) {
			if (authenticationService.authenticationExists(userName)) {
				throw new FinraException.FinraWebScriptException(HttpStatus.BAD_REQUEST, "User" + userName + " already exists.");
			} else {
		        UserTransaction userTransaction = transactionService.getUserTransaction();
		        try {
		            userTransaction.begin();
		            authenticationService.createAuthentication(userName, userPassword.toCharArray());
					Map<QName, Serializable> properties = new HashMap<>();
					properties.put(ContentModel.PROP_USERNAME, userName);
					properties.put(ContentModel.PROP_FIRSTNAME, "firstName");
					properties.put(ContentModel.PROP_LASTNAME, "lastName");
					properties.put(ContentModel.PROP_EMAIL, userName+"@example.com");
					properties.put(ContentModel.PROP_JOBTITLE, "jobTitle");					
					personNodeRef = personService.createPerson(properties);
		            userTransaction.commit();
		        }catch(AlfrescoRuntimeException are){
		        	AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
		        	UserTransaction userTransactionTwo = transactionService.getUserTransaction();
		        	try {
						userTransactionTwo.begin();
						personService.deletePerson(userName);
						userTransactionTwo.commit();
					} catch (Exception e) {
						e.printStackTrace();
					} 
		        	
		        }catch (Exception e) {
		            try {
		                userTransaction.rollback();
		            } catch (Exception e1) {
		            	log.error(e1.getMessage(), e1);
		            }
		            authenticationContext.clearCurrentSecurityContext();
		            throw new AlfrescoRuntimeException("User creation failed", e);
		        } finally {
		            authenticationContext.clearCurrentSecurityContext();
		        }			
			}
		}
		return personNodeRef;
	}

}
