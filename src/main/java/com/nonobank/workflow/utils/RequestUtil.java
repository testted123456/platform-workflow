package com.nonobank.workflow.utils;

import com.alibaba.fastjson.JSONObject;
import com.nonobank.workflow.component.exception.WorkflowException;
import com.nonobank.workflow.component.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestUtil {

    public static Logger logger = LoggerFactory.getLogger(RequestUtil.class);

    public static String getString(JSONObject reqJson, String key, boolean isValidate){
        String value = null;

        logger.info("检查请求参数{}", key);
        if (reqJson.containsKey(key)) {
            value = reqJson.getString(key);
            if (isValidate && value.isEmpty()) {
                throw new WorkflowException(ResultCode.VALIDATION_ERROR.getCode(), String.format("请求提供参数%s为空", key));
            }
        }else{
            if (isValidate) {
                throw new WorkflowException(ResultCode.VALIDATION_ERROR.getCode(), String.format("请求未提供参数%s", key));
            }
        }

        return value;
    }

    public boolean getBooleanValue(JSONObject reqJson, String key, boolean isValidate){
        boolean b = false;

        logger.info("检查请求参数{}", key);
        if (reqJson.containsKey(key)) {
            b = reqJson.getBooleanValue(key);
        }else{
            if (isValidate) {
                throw new WorkflowException(ResultCode.VALIDATION_ERROR.getCode(), String.format("请求未提供参数%s", key));
            }
        }

        return b;
    }

}
