package com.nonobank.workflow.entity;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class Requriment {
	
	@Id
	@GeneratedValue
	Integer id;
	
	@NotEmpty(message="标题不能为空")
	@Column(nullable=false, columnDefinition="varchar(300) COMMENT '需求名称'")
	String name;
	
	@NotEmpty(message="产品线不能为空")
	@Column(nullable=false, columnDefinition="varchar(300) COMMENT '产品线'")
	String productLine;
	
	@NotEmpty(message="需求类型不能为空")
	@Column(nullable=false, columnDefinition="varchar(300) COMMENT '需求类型'")
	String type;
	
	@NotEmpty(message="项目经理不能为空")
	@Column(nullable=false, columnDefinition="varchar(300) COMMENT '项目经理'")
	String projectManager;
	
	@NotEmpty(message="产品经理不能为空")
	@Column(nullable=false, columnDefinition="varchar(300) COMMENT '产品经理'")
	String productManager;
	
	@Column(nullable=true, columnDefinition="datetime COMMENT '开发开始时间'")
	LocalDateTime devStartTime;
	
	@Column(nullable=true, columnDefinition="datetime COMMENT '开发结束时间'")
	LocalDateTime devEndTime;
	
	@Column(nullable=true, columnDefinition="datetime COMMENT '测试开始时间'")
	LocalDateTime testStartTime;
	
	@Column(nullable=true, columnDefinition="datetime COMMENT '测试结束时间'")
	LocalDateTime testEndTime;
	
	@Column(nullable=true, columnDefinition="datetime COMMENT '上线时间'")
	LocalDateTime releaseTime;
	
	@Column(nullable=true, columnDefinition="text COMMENT '开发人员'")
	String developers;
	
	@Column(nullable=true, columnDefinition="text COMMENT '测试人员'")
	String testers;
	
	@Column(nullable=false, columnDefinition="varchar(5) COMMENT '优先级'")
	String priority;
	
	@Column(nullable=false, columnDefinition="varchar(5) COMMENT '需求阶段'")
	String status;
	
	@Column(nullable=false, columnDefinition="text COMMENT '需求内容'")
	String context;
	
	@Column(nullable=false, columnDefinition="varchar(200) COMMENT '项目名称'")
	String projectName;
	
	@Column(nullable=true, columnDefinition="varchar(100) COMMENT '产品验收员'")
	String productReceiver;
	
	@Column(nullable=true, columnDefinition="varchar(100) COMMENT '项目验收员'")
	String projectReceiver;
	
	@Column(nullable=true, columnDefinition="text COMMENT '收件人'")
	String mailReceiver;
	
	@Column(nullable=true, columnDefinition="text COMMENT '产品验收拒绝原因'")
	String rejectReasons1;
	
	@Column(nullable=true, columnDefinition="text COMMENT '业务验收拒绝原因'")
	String rejectReasons2;
	
	@Column(nullable=true, columnDefinition="varchar(20) COMMENT '关联的工作流id'")
	String taskId;
	
	@Column(nullable=true, columnDefinition="text COMMENT '备注'")
	String comment;
	
	@Column(nullable=true, columnDefinition="varchar(100) COMMENT '创建人'")
	String createdBy;
	
	@Column(nullable=false, columnDefinition="smallint(1) COMMENT '0:正常，1:暂停，2:已删除'")
	Short optStatus;

	@OneToMany(mappedBy="requriment", cascade={CascadeType.ALL}, fetch = FetchType.EAGER)
	List<Comment> comments;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProductLine() {
		return productLine;
	}

	public void setProductLine(String productLine) {
		this.productLine = productLine;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(String projectManager) {
		this.projectManager = projectManager;
	}

	public String getProductManager() {
		return productManager;
	}

	public void setProductManager(String productManager) {
		this.productManager = productManager;
	}

	public LocalDateTime getDevStartTime() {
		return devStartTime;
	}

	public void setDevStartTime(LocalDateTime devStartTime) {
		this.devStartTime = devStartTime;
	}

	public LocalDateTime getDevEndTime() {
		return devEndTime;
	}

	public void setDevEndTime(LocalDateTime devEndTime) {
		this.devEndTime = devEndTime;
	}

	public LocalDateTime getTestStartTime() {
		return testStartTime;
	}

	public void setTestStartTime(LocalDateTime testStartTime) {
		this.testStartTime = testStartTime;
	}

	public LocalDateTime getTestEndTime() {
		return testEndTime;
	}

	public void setTestEndTime(LocalDateTime testEndTime) {
		this.testEndTime = testEndTime;
	}

	public LocalDateTime getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(LocalDateTime releaseTime) {
		this.releaseTime = releaseTime;
	}

	public String getDevelopers() {
		return developers;
	}

	public void setDevelopers(String developers) {
		this.developers = developers;
	}

	public String getTesters() {
		return testers;
	}

	public void setTesters(String testers) {
		this.testers = testers;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getRejectReasons1() {
		return rejectReasons1;
	}

	public void setRejectReasons1(String rejectReasons1) {
		this.rejectReasons1 = rejectReasons1;
	}

	public String getRejectReasons2() {
		return rejectReasons2;
	}

	public void setRejectReasons2(String rejectReasons2) {
		this.rejectReasons2 = rejectReasons2;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProductReceiver() {
		return productReceiver;
	}

	public void setProductReceiver(String productReceiver) {
		this.productReceiver = productReceiver;
	}

	public String getProjectReceiver() {
		return projectReceiver;
	}

	public void setProjectReceiver(String projectReceiver) {
		this.projectReceiver = projectReceiver;
	}

	public String getMailReceiver() {
		return mailReceiver;
	}

	public void setMailReceiver(String mailReceiver) {
		this.mailReceiver = mailReceiver;
	}

	public Short getOptStatus() {
		return optStatus;
	}

	public void setOptStatus(Short optStatus) {
		this.optStatus = optStatus;
	}

}
