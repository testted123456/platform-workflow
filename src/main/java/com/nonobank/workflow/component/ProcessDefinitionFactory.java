package com.nonobank.workflow.component;

import java.util.List;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcessDefinitionFactory {
	
	@Autowired
	RepositoryService repositoryService;
	
	public ProcessDefinition getProcessDefinition(String key){
		List<Deployment> dps =
		    	repositoryService.createDeploymentQuery().deploymentName("SpringAutoDeployment").orderByDeploymentName().desc().list();
		
		Deployment dp = dps.get(0);
    	
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().deploymentId(dp.getId()).processDefinitionKey(key).singleResult();

		return pd;
	}

}
