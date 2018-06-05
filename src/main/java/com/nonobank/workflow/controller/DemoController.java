package com.nonobank.workflow.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController  
@RequestMapping("/demo")  
public class DemoController {
	 @Autowired    
	    private RepositoryService repositoryService;   
	 
	    @Autowired    
	    private RuntimeService runtimeService; 
	    
	    @Autowired    
	    private TaskService taskService;    
	    
	    @Autowired
	    private HistoryService historyService;
	    
	    @RequestMapping("/firstDemo")  
	    public void firstDemo() {  
	        //根据bpmn文件部署流程  
	        Deployment deployment = repositoryService.createDeployment().addClasspathResource("testProcess.bpmn").deploy();  
	        
	        //获取流程定义  
	        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();  
	        //启动流程定义，返回流程实例  
	        ProcessInstance pi = runtimeService.startProcessInstanceById(processDefinition.getId());  
	        String processId = pi.getId();  
	        System.out.println("流程创建成功，当前流程实例ID："+processId);  
	        
	        List<Task> tasks = taskService.createTaskQuery().taskCandidateUser("wanger")
	        		.taskCandidateGroup("zhaowu")
	        		.list();
	        
	        for(Task t : tasks){
	        	System.out.println("任务名称：" + t.getName());
	        }
	          
	        Task task=taskService.createTaskQuery().processInstanceId(processId)
//	        		.taskCandidateUser("wanger")
	        		.singleResult();  
	        
	        taskService.setVariable(task.getId(), "请假人", "zhangsan");
	        System.out.println("第一次执行前，任务名称："+task.getName());  
	        System.out.println("第一次执行前，assignee：" + task.getAssignee());
	        System.out.println("第一次执行前，请假人："+ taskService.getVariable(task.getId(), "请假人")); 
	        Map<String, Object> variables = new HashMap<String, Object>(); 
	        variables.put("msg", "important");
	        taskService.complete(task.getId(), variables);  
	        
	        /**
	        task = taskService.createTaskQuery().processInstanceId(processId).singleResult();  
	        System.out.println("第二次执行前，任务名称："+task.getName());  
	        taskService.complete(task.getId());  
	  
	        task = taskService.createTaskQuery().processInstanceId(processId).singleResult();  
	        System.out.println("task为null，任务执行完毕："+task);  **/
	        
	    }
	    
	    @RequestMapping("/secondDemo")  
	    public void secondDemo() {  
	    	List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().processDefinitionKey("testProcess").list();
	    	
	    	for(ProcessDefinition pd : list){
	    		System.out.println(pd.getId());
	    		if(pd.getId().equals("testProcess:1:10")){
	    			ProcessInstance pi =
	    			runtimeService.startProcessInstanceById(pd.getId());
	    			Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
	    			 System.out.println("第一次执行前，任务名称："+task.getName());  
	    		}
	    	}
	    }
	    
	    @RequestMapping("/thirdDemo")  
	    public void thirdDemo(){
	    	List<HistoricTaskInstance> list =
//	    	historyService.createHistoricTaskInstanceQuery().list();
//	    	historyService.createHistoricTaskInstanceQuery().taskName("请假").list();
	    	historyService.createHistoricTaskInstanceQuery().processDefinitionNameLike("testProcess").list();
	    	
	    	for(HistoricTaskInstance ht : list){
	    		System.out.println(ht.getName());
	    	}
	    	
	    	list = historyService.createHistoricTaskInstanceQuery().processDefinitionKeyLike("testProcess").list();
	    	
	    	for(HistoricTaskInstance ht : list){
	    		System.out.println("==" + ht.getName());
	    	}
	    	
	    }
	    
	    @RequestMapping("/forthDemo")  
	    public void forthDemo(){
	    	Deployment dp =
	    	repositoryService.createDeploymentQuery().deploymentName("SpringAutoDeployment").singleResult();
	    	
	    	List<ProcessDefinition> pds = repositoryService.createProcessDefinitionQuery().deploymentId(dp.getId()).list();
//	    			.singleResult();
	    	
	    	ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().deploymentId(dp.getId()).processDefinitionKey("testProcess").singleResult();
	    	
	    	System.out.println(pds.size());
	    	
	    	ProcessInstance pi = runtimeService.createProcessInstanceQuery().processDefinitionId(pd.getId()).singleResult();
	    	
	    	System.out.println(pi.getName());
	    }
}
