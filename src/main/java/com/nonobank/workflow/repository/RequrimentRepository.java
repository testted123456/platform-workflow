package com.nonobank.workflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nonobank.workflow.entity.Requriment;

public interface RequrimentRepository extends JpaRepository<Requriment, Integer> {
	
	Requriment findById(Integer id);

}
