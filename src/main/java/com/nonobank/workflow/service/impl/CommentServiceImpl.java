package com.nonobank.workflow.service.impl;

import com.nonobank.workflow.entity.Comment;
import com.nonobank.workflow.entity.Requriment;
import com.nonobank.workflow.repository.CommentRepository;
import com.nonobank.workflow.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentRepository commentRepository;

    public Comment save(Comment comment){
        comment = commentRepository.save(comment);
        return comment;
    }

    public Comment getById(Integer id){
        Comment comment = commentRepository.findById(id);
        return comment;
    }

    public List<Comment> findByRequirementId(int reqId){
        return commentRepository.findByRequrimentIdEquals(reqId);
    }

}
