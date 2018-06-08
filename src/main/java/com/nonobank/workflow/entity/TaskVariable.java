package com.nonobank.workflow.entity;

public enum TaskVariable {
    SUBMITTER("submitter")
    ;

    private String name;

    TaskVariable(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

}
