package finra.common.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import finra.create.fileanduser.CreateFileAndUser;

@Component("FinraNodeLocator")
public class FinraNodeLocator {

	public static Logger log = LoggerFactory.getLogger(FinraNodeLocator.class);

	@Autowired
	@Qualifier(value = "repositoryHelper")
	protected Repository repositoryHelper;

	@Autowired
	@Qualifier(value = "ServiceRegistry")
	protected ServiceRegistry serviceRegistry;

	public NodeRef getCompanyHomeFolder() {
		return repositoryHelper.getCompanyHome();
	}

	public NodeRef getGuestHomeFolder() {
		List<String> pathElements = new ArrayList<String>();
		pathElements.add("Guest Home");
		NodeRef nr = null;
		try {
			nr = serviceRegistry.getFileFolderService().resolveNamePath(getCompanyHomeFolder(), pathElements).getNodeRef();
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		}
		return nr;
	}

	public NodeRef locateNodeRef(NodeRef nodeRef, String... paths) {
		NodeRef resultNode = null;
		try {
			List<String> pathElements = Arrays.asList(paths);
			resultNode = serviceRegistry.getFileFolderService().resolveNamePath(nodeRef, pathElements).getNodeRef();
		} catch (FileNotFoundException e) {
			Path primaryPath = serviceRegistry.getNodeService().getPath(nodeRef);
			if (CreateFileAndUser.log.isDebugEnabled()) {
				CreateFileAndUser.log.debug("Requested File '" + primaryPath.toString() + "' Not Found");
			}
		}
		return resultNode;
	}

}
