package com.nonobank.workflow.service;

import java.util.List;

import org.activiti.engine.history.HistoricActivityInstance;

import com.nonobank.workflow.entity.Requriment;

public interface RequrimentService {
	
	public Requriment add(Requriment Requriment);
	
	public Requriment getById(Integer id);

	public List<HistoricActivityInstance> getHistoryById(Integer id);
	
	public Requriment update(Requriment Requriment);
	
	public List<Requriment> getAll();
	
	//提交评审，进入待评审状态
	public Requriment submitReView(Requriment Requriment);
	
	//评审完成，进入待排期状态
	public Requriment finishReView(Requriment Requriment);
}
