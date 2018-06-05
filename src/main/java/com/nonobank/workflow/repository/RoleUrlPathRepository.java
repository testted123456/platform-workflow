package com.nonobank.workflow.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.nonobank.workflow.entity.Requriment;

public interface RoleUrlPathRepository extends JpaRepository<Requriment, Integer> {
	
    @Query(value="select url_path, role_name from role_url_path where system='testCase' and optstatus!=2 ", nativeQuery = true)
    List<Object[]> findUrlAndRole();
}
