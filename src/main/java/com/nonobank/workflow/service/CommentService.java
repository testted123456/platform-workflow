package com.nonobank.workflow.service;

import com.nonobank.workflow.entity.Comment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {

    public Comment save(Comment comment);

    public Comment getById(Integer id);

    public List<Comment> findByRequirementId(int reqId);

}
