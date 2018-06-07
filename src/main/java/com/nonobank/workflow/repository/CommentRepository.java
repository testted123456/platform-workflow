package com.nonobank.workflow.repository;

import com.nonobank.workflow.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Comment findById(Integer id);

    List<Comment> findByRequrimentIdEquals(int requirementId);

}
