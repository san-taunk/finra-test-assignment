package finra.common.impl;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import finra.dto.GrantReadAccessDto;

@Component("FinraGrantReadAccessToUser")
public class FinraGrantReadAccessToUser {

    public static Logger log = LoggerFactory.getLogger(FinraGrantReadAccessToUser.class);
	
	@Autowired
	@Qualifier("TransactionService")
	public TransactionService transactionService;
	
	@Autowired
	@Qualifier(value = "ServiceRegistry")
	protected ServiceRegistry serviceRegistry;
	
	@Autowired
	@Qualifier("PermissionService")
	public PermissionService permissionService;	

	public void grantReadAccessToUser(final GrantReadAccessDto finalGrantReadAccessDto) {
		RetryingTransactionHelper txnHelper = transactionService.getRetryingTransactionHelper();
		RetryingTransactionHelper.RetryingTransactionCallback<Void> callback
		        = new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
		            @Override
		            public Void execute() throws Throwable {
		                return AuthenticationUtil.runAs(new RunAsWork<Void>() {
		                    @Override
		                    public Void doWork() throws Exception {
		                    	String nodeRef = finalGrantReadAccessDto.getNodeRef();
		                    	String userId = finalGrantReadAccessDto.getUserId();
		                    	NodeRef nr1 = new NodeRef(nodeRef);
		                    	log.debug("### Granting : " + userId + " and " + nodeRef + " ");
		                    	permissionService.setPermission(nr1, userId, PermissionService.CONSUMER, true);
		                        return null;
		                    }
		                }, AuthenticationUtil.SYSTEM_USER_NAME);
		            }
		        };
		txnHelper.doInTransaction(callback, false, true);
	}
}
