package com.nonobank.workflow.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import com.nonobank.workflow.component.exception.WorkflowException;
import com.nonobank.workflow.entity.Comment;
import com.nonobank.workflow.entity.RequrimentTask;
import com.nonobank.workflow.entity.TaskVariable;
import com.nonobank.workflow.service.CommentService;
import com.nonobank.workflow.utils.CommonUtil;
import com.nonobank.workflow.utils.RequestUtil;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
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
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value="requriment")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RequrimentController {
	
	public static Logger logger = LoggerFactory.getLogger(RequrimentController.class);

	@Autowired
	RequrimentService requrimentService;

	@Autowired
	CommentService commentService;
	
	@Autowired
	ProcessDefinitionFactory processDefinitionFactory;
	
	@Autowired
	RuntimeService runtimeService;
	
	@Autowired    
	private TaskService taskService; 
	
	@Autowired
	private HistoryService historyService;

	@Autowired
	private IdentityService identityService;


	@PostMapping(value="add")
	@ResponseBody
	public Result add(@RequestBody Requriment requriment){
		logger.info("开始新增需求：{}", requriment.getName());
		
		String userName = UserUtil.getUser();
		String comment = requriment.getComment();
		//这个方法默认返回第一个流程定义
		//ProcessDefinition pd = processDefinitionFactory.getProcessDefinition("requriment");
		Map<String, Object> variables = new HashMap<>();
		variables.put("userId", userName);
		variables.put("comment", comment);

		//用最新的流程定义版本实例化流程
		//ProcessInstance pi = runtimeService.startProcessInstanceById(pd.getId(), variables);
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("requriment", variables);

		Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
		
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
		taskService.setVariableLocal(task.getId(), TaskVariable.SUBMITTER.getName(), UserUtil.getUser());
		taskService.complete(task.getId(), variables);
		task = taskService.createTaskQuery().processInstanceId(taskId).singleResult();
		requriment.setStatus(task.getName());
		requriment = requrimentService.add(requriment);
		return ResultUtil.success(requriment);
	}


	/**
	 * @param reqJson
	 * {
	 *     requirementId：1001
	 * }
	 * @return
	 */
	@PostMapping(value="startReview")
	@ResponseBody
	public Result startReview(@RequestBody JSONObject reqJson){
		String requirementId = null;

		logger.info("检查请求参数requirementId");
		if (reqJson.containsKey("requirementId")) {
			requirementId = reqJson.getString("requirementId");
			if (requirementId.isEmpty()){
				return ResultUtil.error(ResultCode.VALIDATION_ERROR.getCode(), "请求提供参数requirementId为空");
			}

		}else{
			return ResultUtil.error(ResultCode.VALIDATION_ERROR.getCode(), "请求未提供参数requirementId");
		}

		Requriment req  = requrimentService.getById(Integer.valueOf(requirementId));
		logger.info("需求\"{}\"开始评审", req.getName());


		String processId = req.getTaskId();
		Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
		if (task.getTaskDefinitionKey().equals(RequrimentTask.PENDING_REVIEW.getId())){
			Map<String, Object> variables = new HashMap<>();
			variables.put("comment", req.getComment());
			taskService.setVariableLocal(task.getId(),TaskVariable.SUBMITTER.getName(), UserUtil.getUser());
			taskService.complete(task.getId(), variables);
			task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
			//TODO
			//Set Assignee and Candidate

			req.setStatus(task.getName());
			req = requrimentService.add(req);
		}else{
			return ResultUtil.error(ResultCode.VALIDATION_ERROR.getCode(), "需求流程当前任务是" + task.getName() + "和请求开始评审不匹配");
		}

		return ResultUtil.success(req);
	}

	@PostMapping(value="startSchedule")
	@ResponseBody
	public Result startSchedule(@RequestBody JSONObject reqJson){
		String requirementId = null;

		logger.info("检查请求参数requirementId");
		if (reqJson.containsKey("requirementId")) {
			requirementId = reqJson.getString("requirementId");
			if (requirementId.isEmpty()){
				return ResultUtil.error(ResultCode.VALIDATION_ERROR.getCode(), "请求提供参数requirementId为空");
			}
		}else{
			return ResultUtil.error(ResultCode.VALIDATION_ERROR.getCode(), "请求未提供参数requirementId");
		}

		Requriment req  = requrimentService.getById(Integer.valueOf(requirementId));
		logger.info("需求\"{}\"开始排期", req.getName());

		String processId = req.getTaskId();
		Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
		if (task.getTaskDefinitionKey().equals(RequrimentTask.PENDING_SCHEDULE.getId())){
			Map<String, Object> variables = new HashMap<>();
			variables.put("comment", req.getComment());
			taskService.setVariableLocal(task.getId(),TaskVariable.SUBMITTER.getName(), UserUtil.getUser());
			taskService.complete(task.getId(), variables);
			task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
			//TODO
			//Set Assignee and Candidate

			req.setStatus(task.getName());
			req = requrimentService.add(req);
		}else{
			return ResultUtil.error(ResultCode.VALIDATION_ERROR.getCode(), "需求流程当前任务是" + task.getName() + "和请求开始评审不匹配");
		}

		return ResultUtil.success(req);
	}

	@PostMapping(value="submitSchedule")
	@ResponseBody
	public Result submitSchedule(@RequestBody JSONObject reqJson){
		return ResultUtil.success(null);
	}

	@PostMapping(value="submitDevelop")
	@ResponseBody
	public Result submitDevelop(@RequestBody JSONObject reqJson){
		return ResultUtil.success(null);
	}

	@PostMapping(value="submitSTBTest")
	@ResponseBody
	public Result submitSTBTest(@RequestBody JSONObject reqJson){
		return ResultUtil.success(null);
	}

	@PostMapping(value="submitSITTest")
	@ResponseBody
	public Result submitSITTest(@RequestBody JSONObject reqJson){
		return ResultUtil.success(null);
	}

	@PostMapping(value="submitPRETest")
	@ResponseBody
	public Result submitPRETest(@RequestBody JSONObject reqJson){
		return ResultUtil.success(null);
	}

	@PostMapping(value="acceptProduct")
	@ResponseBody
	public Result acceptProduct(@RequestBody JSONObject reqJson){

		String requirementId = null;
		boolean action = false;

		logger.info("检查请求参数requirementId");
		if (reqJson.containsKey("requirementId")) {
			requirementId = reqJson.getString("requirementId");
			if (requirementId.isEmpty()){
				return ResultUtil.error(ResultCode.VALIDATION_ERROR.getCode(), "请求提供参数requirementId为空");
			}

		}else{
			return ResultUtil.error(ResultCode.VALIDATION_ERROR.getCode(), "请求未提供参数requirementId");
		}

		logger.info("检查请求参数action");
		if (reqJson.containsKey("action")) {
			action = reqJson.getBooleanValue("action");
			if (requirementId.isEmpty()){
				return ResultUtil.error(ResultCode.VALIDATION_ERROR.getCode(), "请求提供参数action为空");
			}

		}else{
			return ResultUtil.error(ResultCode.VALIDATION_ERROR.getCode(), "请求未提供参数action");
		}

		Requriment req  = requrimentService.getById(Integer.valueOf(requirementId));
		logger.info("需求\"{}\"开始{}", req.getName(), RequrimentTask.PRODUCT_ACCEPT.getName());

		String processId = req.getTaskId();
		Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
		if (task.getTaskDefinitionKey().equals(RequrimentTask.PRODUCT_ACCEPT.getId())){
			Map<String, Object> variables = new HashMap<>();
			variables.put("comment", req.getComment());
			variables.put("result", action);
			taskService.setVariableLocal(task.getId(),TaskVariable.SUBMITTER.getName(), UserUtil.getUser());
			taskService.complete(task.getId(), variables);
			task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
			//TODO
			//Set Assignee and Candidate

			req.setStatus(task.getName());
			req = requrimentService.add(req);
		}else{
			return ResultUtil.error(ResultCode.VALIDATION_ERROR.getCode(), "需求流程当前任务是" + task.getName() + "和请求产品验收不匹配");
		}

		return ResultUtil.success(req);
	}

	@PostMapping(value="acceptBusiness")
	@ResponseBody
	public Result acceptBusiness(@RequestBody JSONObject reqJson){

		String requirementId = null;
		String action = null;

		logger.info("检查请求参数requirementId");
		if (reqJson.containsKey("requirementId")) {
			requirementId = reqJson.getString("requirementId");
			if (requirementId.isEmpty()){
				return ResultUtil.error(ResultCode.VALIDATION_ERROR.getCode(), "请求提供参数requirementId为空");
			}

		}else{
			return ResultUtil.error(ResultCode.VALIDATION_ERROR.getCode(), "请求未提供参数requirementId");
		}

		logger.info("检查请求参数action");
		if (reqJson.containsKey("action")) {
			action = reqJson.getString("action");
			if (requirementId.isEmpty()){
				return ResultUtil.error(ResultCode.VALIDATION_ERROR.getCode(), "请求提供参数action为空");
			}

		}else{
			return ResultUtil.error(ResultCode.VALIDATION_ERROR.getCode(), "请求未提供参数action");
		}

		Requriment req  = requrimentService.getById(Integer.valueOf(requirementId));
		logger.info("需求\"{}\"开始{}", req.getName(), RequrimentTask.BUSINESS_ACCEPT.getName());

		String processId = req.getTaskId();
		Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
		if (task.getTaskDefinitionKey().equals(RequrimentTask.BUSINESS_ACCEPT.getId())){
			Map<String, Object> variables = new HashMap<>();
			variables.put("comment", req.getComment());
			variables.put("result", Boolean.valueOf(action));
			taskService.setVariableLocal(task.getId(),TaskVariable.SUBMITTER.getName(), UserUtil.getUser());
			taskService.complete(task.getId(), variables);
			task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
			//TODO
			//Set Assignee and Candidate

			req.setStatus(task.getName());
			req = requrimentService.add(req);
		}else{
			return ResultUtil.error(ResultCode.VALIDATION_ERROR.getCode(), "需求流程当前任务是" + task.getName() + "和请求产品验收不匹配");
		}

		return ResultUtil.success(req);
	}

	@PostMapping(value="submitOnline")
	@ResponseBody
	public Result submitOnline(@RequestBody JSONObject reqJson){
		return ResultUtil.success(null);
	}

	/*
	@PostMapping(value="addActivitiComment")
	@ResponseBody
	public Result addActivitiComment(@RequestBody JSONObject reqJson){
		User user = identityService.createUserQuery().userId(UserUtil.getUser()).singleResult();
		if (user == null) {
			user = identityService.newUser(UserUtil.getUser());
			identityService.saveUser(user);
		}
		//Passes the authenticated user id for this particular thread
		identityService.setAuthenticatedUserId(user.getId());

		String requirementId = RequestUtil.getString(reqJson, "requirementId", true);
		String comment = RequestUtil.getString(reqJson, "comment", true);

		Requriment req  = requrimentService.getById(Integer.valueOf(requirementId));
		String processId = req.getTaskId();
		Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
		taskService.addComment(task.getId(), processId, comment);
		return ResultUtil.success();
	}

	@GetMapping(value="getActivitiCommentList")
	@ResponseBody
	public Result getActivitiCommentList(String requirementId){
		Requriment req  = requrimentService.getById(Integer.valueOf(requirementId));
		String processId = req.getTaskId();
		List<Comment> commentList = taskService.getProcessInstanceComments(processId);
		List<?> list = commentList.stream().map( c -> {
				HashMap<String, Object> map = new HashMap<>();
				map.put("id", c.getId());
				map.put("time", c.getTime());
				map.put("user", c.getUserId());
				map.put("message", c.getFullMessage());
				return map;
		}).collect(Collectors.toList());

		return ResultUtil.success(list);
	}
	*/


	@PostMapping(value="addComment")
	@ResponseBody
	public Result addComment(@RequestBody JSONObject reqJson){
		String requirementId = RequestUtil.getString(reqJson, "requirementId", true);
		String comment = RequestUtil.getString(reqJson, "comment", true);

		Comment com = new Comment();
		Requriment req = requrimentService.getById(Integer.valueOf(requirementId));
		com.setRequriment(req);
		//com.setTime(LocalDateTime.now());
		com.setUser(UserUtil.getUser());
		com.setComment(comment);
		commentService.save(com);
		return ResultUtil.success("添加评论成功");
	}


	@GetMapping(value="getCommentList")
	@ResponseBody
	public Result getReqCommentList(String requirementId){

		if(requirementId != null){
			if (requirementId.isEmpty()){
				throw new WorkflowException(ResultCode.VALIDATION_ERROR.getCode(), String.format("请求提供参数%s为空", "requirementId"));
			}
		}else{
			throw new WorkflowException(ResultCode.VALIDATION_ERROR.getCode(), String.format("请求未提供参数%s", "requirementId"));
		}

		List<Comment> commentList = commentService.findByRequirementId(Integer.valueOf(requirementId));
		List list = commentList.stream().map( c -> {
			Map map = new HashMap();
			map.put("id", c.getId());
			map.put("user", c.getUser());
			//map.put("time", CommonUtil.Time2String(c.getTime()));
			map.put("comment", c.getComment());
			return map;
		}).collect(Collectors.toList());

		return ResultUtil.success(list);
	}

	@PostMapping(value="updateComment")
	@ResponseBody
	public Result updateComment(@RequestBody Comment comment){
		commentService.save(comment);
		return ResultUtil.success("更新评论成功");
	}


	/**
	 * 仅供debug
	 * @param reqJson
	 * @return
	 */
	@PostMapping(value="nextTask")
	@ResponseBody
	public Result nextTask(@RequestBody JSONObject reqJson){

		String requirementId = null;

		logger.info("检查请求参数requirementId");
		if (reqJson.containsKey("requirementId")) {
			requirementId = reqJson.getString("requirementId");
			if (requirementId.isEmpty()){
				return ResultUtil.error(ResultCode.VALIDATION_ERROR.getCode(), "请求提供参数requirementId为空");
			}

		}else{
			return ResultUtil.error(ResultCode.VALIDATION_ERROR.getCode(), "请求未提供参数requirementId");
		}

		Requriment req  = requrimentService.getById(Integer.valueOf(requirementId));

		String taskId = req.getTaskId();
		Task task = taskService.createTaskQuery().processInstanceId(taskId).singleResult();
		Map<String, Object> variables = new HashMap<>();
		variables.put("comment", req.getComment());
		taskService.setVariableLocal(task.getId(),TaskVariable.SUBMITTER.getName(), UserUtil.getUser());
		taskService.complete(task.getId(), variables);
		task = taskService.createTaskQuery().processInstanceId(taskId).singleResult();
		req.setStatus(task.getName());
		req = requrimentService.add(req);
		return ResultUtil.success(req);
	}

	public static void main(String [] args){
//		LocalDateTime ldt = LocalDateTime.parse("1528109873000");
//		
//		System.out.println(ldt.format(DateTimeFormatter.BASIC_ISO_DATE));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		System.out.println(sdf.format(new Date(Long.valueOf("1528109873000"))));
	}
	
}
