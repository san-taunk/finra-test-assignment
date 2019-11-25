package finra.common.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;
import org.springframework.stereotype.Component;

@Component("FinraFileCreator")
public class FinraFileCreator {

	@Autowired
	@Qualifier("TransactionService")
	public TransactionService transactionService;
	
	@Autowired
	@Qualifier(value = "ServiceRegistry")
	protected ServiceRegistry serviceRegistry;
	
	public NodeRef createFile(final String fileName, final NodeRef parentFolder, final FormField finalContent) {
		RetryingTransactionHelper txnHelper = transactionService.getRetryingTransactionHelper();
		RetryingTransactionHelper.RetryingTransactionCallback<NodeRef> callback
		        = new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
		            @Override
		            public NodeRef execute() throws Throwable {
		                return AuthenticationUtil.runAs(new RunAsWork<NodeRef>() {
		                    @Override
		                    public NodeRef doWork() throws Exception {
		                        ContentWriter tempWriter = serviceRegistry.getContentService().getTempWriter();
		                        tempWriter.putContent(finalContent.getInputStream());
		                        Map<QName, Serializable> repositoryMap = new HashMap<>();
		                        repositoryMap.put(ContentModel.PROP_NAME, fileName);
		                        QName lclQname = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, fileName);
		                        ChildAssociationRef newNodeAssoc = serviceRegistry.getNodeService().createNode(parentFolder, ContentModel.ASSOC_CONTAINS,lclQname, ContentModel.TYPE_CONTENT, repositoryMap);
		                        NodeRef resultNode = newNodeAssoc.getChildRef();
		                        ContentWriter writer = serviceRegistry.getContentService().getWriter(resultNode, ContentModel.PROP_CONTENT,true);
		                        writer.putContent(tempWriter.getReader());
		                        return resultNode;
		                    }
		                }, AuthenticationUtil.SYSTEM_USER_NAME);
		            }
		        };
		return txnHelper.doInTransaction(callback, false, true);
	}	
	
}
