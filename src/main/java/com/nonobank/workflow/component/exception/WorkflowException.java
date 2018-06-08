package com.nonobank.workflow.component.exception;

import com.nonobank.workflow.component.result.ResultCode;

public class WorkflowException extends RuntimeException {

	private static final long serialVersionUID = 2495559366495945814L;
	private int code;

	public WorkflowException(ResultCode resultCode) {
		super(resultCode.getMsg());
		this.code = resultCode.getCode();
	}
	
	public WorkflowException(int code, String msg){
		super(msg);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
