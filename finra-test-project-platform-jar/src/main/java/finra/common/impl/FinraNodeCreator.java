package finra.common.impl;

import java.util.Arrays;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("FinraNodeCreator")
public class FinraNodeCreator {

	@Autowired
	@Qualifier(value = "ServiceRegistry")
	public ServiceRegistry serviceRegistry;

	@Autowired
	@Qualifier(value = "FinraNodeLocator")
	public FinraNodeLocator finraNodeLocator;

	@Autowired
	@Qualifier("TransactionService")
	public TransactionService transactionService;

	public NodeRef createOrGetFinraFolder() {
		String[] fileCreationPath = new String[] { "Finra" };
		List<String> pathElements = Arrays.asList(fileCreationPath);
		NodeRef caseFolderNodeRef = null;
		try {
			caseFolderNodeRef = serviceRegistry.getFileFolderService().resolveNamePath(finraNodeLocator.getGuestHomeFolder(), pathElements).getNodeRef();
		} catch (FileNotFoundException e) {
			caseFolderNodeRef = createFolderNode(finraNodeLocator.getGuestHomeFolder(), "Finra", ContentModel.TYPE_FOLDER);
		}
		return caseFolderNodeRef;
	}

	public NodeRef createFolderNode(final NodeRef parentNodeRef, final String folderName, final QName caseFolderQname) {
		NodeRef nodeRef = finraNodeLocator.locateNodeRef(parentNodeRef, folderName);
		if (nodeRef == null) {
			try {
				RetryingTransactionHelper txnHelper = transactionService.getRetryingTransactionHelper();
				RetryingTransactionHelper.RetryingTransactionCallback<NodeRef> callback = new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
					@Override
					public NodeRef execute() throws Throwable {
						return AuthenticationUtil.runAs(new RunAsWork<NodeRef>() {
							@Override
							public NodeRef doWork() throws Exception {
								FileInfo fileInfo = serviceRegistry.getFileFolderService().create(parentNodeRef, folderName, caseFolderQname);
								return fileInfo.getNodeRef();
							}
						}, AuthenticationUtil.SYSTEM_USER_NAME);
					}
				};
				nodeRef = txnHelper.doInTransaction(callback, false, true);
			} catch (FileExistsException exception) {
				nodeRef = finraNodeLocator.locateNodeRef(parentNodeRef, folderName);
			}
		}
		return nodeRef;
	}

}
