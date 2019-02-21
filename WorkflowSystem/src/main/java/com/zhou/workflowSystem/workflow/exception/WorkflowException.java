package com.zhou.workflowSystem.workflow.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * 
 * @Title:自定义流程Exception
 * @Author:Administrator
 * @Since:2018年7月11日
 * @Version:1.1.0
 */
public class WorkflowException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 重载构造函数
     */
    public WorkflowException() {
        super();
    }
    
    public WorkflowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    public WorkflowException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public WorkflowException(String message) {
        super(message);
    }
    
    public WorkflowException(Throwable cause) {
        super(cause);
    }
    
    /**
     * 重写父类的方法
     */
    
    @Override
    public String getMessage() {
        return super.getMessage();
    }
    
    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage();
    }
    
    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }
    
    @Override
    public synchronized Throwable initCause(Throwable cause) {
        return super.initCause(cause);
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
    
    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }
    
    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
    }
    
    @Override
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
    }
    
    @Override
    public synchronized Throwable fillInStackTrace() {
        return super.fillInStackTrace();
    }
    
    @Override
    public StackTraceElement[] getStackTrace() {
        return super.getStackTrace();
    }
    
    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) {
        super.setStackTrace(stackTrace);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    
    @Override
    protected Object clone()
        throws CloneNotSupportedException {
        return super.clone();
    }
    
    @Override
    protected void finalize()
        throws Throwable {
        super.finalize();
    }
}
