package com.nonobank.workflow.service.impl;

import java.util.List;

import org.activiti.engine.history.HistoricActivityInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nonobank.workflow.entity.Requriment;
import com.nonobank.workflow.repository.RequrimentRepository;
import com.nonobank.workflow.service.RequrimentService;

@Service
public class RequrimentServiceImpl implements RequrimentService {
	
	@Autowired
	RequrimentRepository requrimentRepository;

	@Override
	public Requriment add(Requriment requriment) {
		requriment = requrimentRepository.save(requriment);
		return requriment;
	}

	@Override
	public Requriment getById(Integer id) {
		return requrimentRepository.findById(id);
	}

	@Override
	public List<HistoricActivityInstance> getHistoryById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Requriment update(Requriment Requriment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Requriment submitReView(Requriment Requriment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Requriment finishReView(Requriment Requriment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Requriment> getAll() {
		return requrimentRepository.findAll();
	}

}
