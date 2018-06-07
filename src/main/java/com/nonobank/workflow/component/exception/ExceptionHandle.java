package com.nonobank.workflow.component.exception;


import com.nonobank.workflow.component.result.Result;
import com.nonobank.workflow.component.result.ResultCode;
import com.nonobank.workflow.component.result.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionHandle {

	public static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);
	
	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public Result handle(Exception e){
		if(null != e.getStackTrace()){
			logger.error(e.getStackTrace().toString());
		}
		
		if( e instanceof WorkflowException){
			WorkflowException wfException = (WorkflowException)e;
			return ResultUtil.error(wfException.getCode(), wfException.getMessage());
		}else{
			logger.error("发生未知异常");
		    e.printStackTrace();
			return ResultUtil.error(ResultCode.UNKOWN_ERROR.getCode(), e.getClass().getName());
		}
	}
}
