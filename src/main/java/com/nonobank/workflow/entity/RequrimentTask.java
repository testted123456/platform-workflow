package com.nonobank.workflow.entity;

public enum RequrimentTask {

    HANDLING("handling", "处理中"),
    PENDING_REVIEW("pendingReview", "待评审"),
    REVIEWING("reviewing", "评审中"),
    PENDING_SCHEDULE("pendingSchedule", "待排期"),
    SCHEDULING("scheduling", "排期中"),
    DEVELOPING("developing", "开发中"),
    STB_ENV_TESTING("stbTesting", "stb测试"),
    SIT_ENV_TESTING("sitTesting", "sit测试"),
    PRE_ENV_TESTING("preTesting", "pre测试"),
    PRODUCT_ACCEPT("productAccept", "产品验收"),
    BUSINESS_ACCEPT("businessAccept", "业务验收"),
    PENDING_ONLINE("pendingOnline", "待上线"),
    ONLINE("online", "已上线")
    ;

    private String id;
    private String name;

    RequrimentTask(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName(){
        return name;
    }

}
