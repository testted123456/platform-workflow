package com.nonobank.workflow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Comment {

    @Id
    @GeneratedValue
    Integer id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "requrimentId", nullable = false)
    Requriment requriment;

    @NotEmpty(message="用户不能为空")
    @Column(nullable=false, columnDefinition="varchar(255) COMMENT '用户'")
    String user;

    @NotNull(message="时间不能为空")
    @Column(nullable=true, columnDefinition="datetime")
    LocalDateTime time;

    @NotEmpty(message="评论不能为空")
    @Column(nullable=false, columnDefinition="text COMMENT '评论'")
    String comment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Requriment getRequriment() {
        return requriment;
    }

    public void setRequriment(Requriment requriment) {
        this.requriment = requriment;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
