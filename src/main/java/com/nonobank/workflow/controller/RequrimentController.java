package com.nonobank.workflow.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nonobank.workflow.component.ProcessDefinitionFactory;
import com.nonobank.workflow.component.result.Result;
import com.nonobank.workflow.component.result.ResultCode;
import com.nonobank.workflow.component.result.ResultUtil;
import com.nonobank.workflow.entity.Requriment;
import com.nonobank.workflow.service.RequrimentService;
import com.nonobank.workflow.utils.UserUtil;

@Controller
@RequestMapping(value="requriment")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RequrimentController {
	
	public static Logger logger = LoggerFactory.getLogger(RequrimentController.class);

	@Autowired
	RequrimentService requrimentService;
	
	@Autowired
	ProcessDefinitionFactory processDefinitionFactory;
	
	@Autowired
	RuntimeService runtimeService;
	
	@Autowired    
	private TaskService taskService; 
	
	 @Autowired
	 private HistoryService historyService;
	 
	 @Autowired
	 IdentityService identityService;
	
	@PostMapping(value="add")
	@ResponseBody
	public Result add(@RequestBody Requriment requriment){
		logger.info("开始新增需求：{}", requriment.getName());
		
		String userName = UserUtil.getUser();
		String comment = requriment.getComment();
		ProcessDefinition pd = processDefinitionFactory.getProcessDefinition("requriment");
		Map<String, Object> variables = new HashMap<>();
		variables.put("userId", userName);
		variables.put("comment", comment);
	
		ProcessInstance pi =
    			runtimeService.startProcessInstanceById(pd.getId(), variables);
		Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
		identityService.newUser("");
		Authentication.setAuthenticatedUserId("");
		
//		taskService.setVariablesLocal(task.getId(), variables);
//		taskService.setVariables(task.getId(), variables);
//		task.setAssignee("userName");
		requriment.setTaskId(pi.getId());
		requriment.setStatus(task.getName());
		requriment.setOptStatus((short)0);
		requriment.setCreatedBy(userName);
		requriment = requrimentService.add(requriment);
		return ResultUtil.success(requriment);
	}
	
	@PostMapping(value="update")
	@ResponseBody
	public Result update(@RequestBody Requriment requriment){
		if(requrimentService.getById(requriment.getId()) != null){
			logger.info("开始更新需求：{}", requriment.getName());
			requriment = requrimentService.add(requriment);
			return ResultUtil.success(requriment);
		}else{
			logger.error("要更新的需求{}不存在", requriment.getName());
			return ResultUtil.error(ResultCode.EMPTY_ERROR.getCode(), "要更新的需求不存在");
		}
	}
	
	@GetMapping(value="getById")
	@ResponseBody
	public Result getById(@RequestParam Integer id){
		Requriment requriment = requrimentService.getById(id);
		String taskId = requriment.getTaskId();
		Task task = taskService.createTaskQuery().processInstanceId(taskId).singleResult();
//		taskId(taskId).singleResult();
		
		System.out.println(task.getName());
		return ResultUtil.success(requriment);
	}

	@GetMapping(value="getHistory")
	@ResponseBody
	public Result getHistory(@RequestParam Integer id){
		Requriment requriment = requrimentService.getById(id);
		String taskId = requriment.getTaskId();

		List<HistoricActivityInstance> list = 
		historyService.createHistoricActivityInstanceQuery().processInstanceId(taskId).orderByHistoricActivityInstanceStartTime().asc().list();
	
//		List<Map<String, String>> map = new ArrayLis
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		
		List<Map<String, String>> l = 
		list.stream().map(x->{
			Map<String, String> map = new HashMap<>();
			map.put("time", sdf.format(x.getStartTime()));
			map.put("name", x.getActivityName());
			map.put("assignee", x.getAssignee());
			return map;
		}).collect(Collectors.toList());
		
		return ResultUtil.success(l);
	}
	
	@GetMapping(value="getAll")
	@ResponseBody
	public Result getAll(){
		logger.info("开始查找所有需求");
		return ResultUtil.success(requrimentService.getAll());
	}
	
	@PostMapping(value="submitReview")
	@ResponseBody
	public Result submitReview(@RequestBody Requriment requriment){
//		requriment = requrimentService.getById(requriment.getId());
		logger.info("需求\"{}\"提交评审", requriment.getName());
		
		String taskId = requriment.getTaskId();
		Task task = taskService.createTaskQuery().processInstanceId(taskId).singleResult();
		Map<String, Object> variables = new HashMap<>();
		variables.put("comment", requriment.getComment());
		taskService.complete(task.getId(), variables);
		task = taskService.createTaskQuery().processInstanceId(taskId).singleResult();
		requriment.setStatus(task.getName());
		requriment = requrimentService.add(requriment);
		return ResultUtil.success(requriment);
	}
	
	public static void main(String [] args){
//		LocalDateTime ldt = LocalDateTime.parse("1528109873000");
//		
//		System.out.println(ldt.format(DateTimeFormatter.BASIC_ISO_DATE));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		System.out.println(sdf.format(new Date(Long.valueOf("1528109873000"))));
	}
	
}
