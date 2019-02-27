package com.zhou.workflowSystem.workflow.entity;

import java.util.Date;
import java.util.Map;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.zhou.workflowSystem.workflow.model.CustomActivitiTask;
import com.zhou.workflowSystem.workflow.model.CustomProcessDefinition;
import com.zhou.workflowSystem.workflow.model.CustomProcessInstance;

import lombok.Data;

/**
 * 封装我的工作模块查询的列表
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年2月23日
 * @Version:1.1.0
 */
@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyWorkEntity {
    
    /**
     * 通用属性
     */
    private String userId;
    
    //流程变量
    private Map<String, Object> variables;
    
    // 流程任务
    //    @JsonIgnore
    //    private Task task;
    
    // 运行中的流程实例
    //    @JsonIgnore
    //    private ProcessInstance processInstance;
    
    /**
     * 在办工作表格属性
     */
    //业务办理号，使用流程实例id充当
    private String businessAcceptNumber;
    
    /**
     * 在办工作列表属性
     */
    private String taskId;
    
    //任务的类型标识
    private String taskType;
    
    //业务名称，使用流程定义名称充当
    private String businessName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date businessStartTime;
    
    private String taskName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date taskStartTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date claimTime;
    
    private String remarkContent;
    
    /**
     * 个人已办列表属性
     */
    private String durationTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date taskEndTime;
    
    /**
     * 办结业务属性
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date businessEndTime;
    
    /**
     * 设置在办工作列表的返回数据
     * 
     * @param historicProcessInstance
     * @param task
     * @param historicTaskInstance
     * @Description:
     */
    public void setHanglingWorkAttributes(HistoricProcessInstance historicProcessInstance,
        HistoricTaskInstance historicTaskInstance) {
        this.businessAcceptNumber = historicProcessInstance.getId();
        this.businessName = historicProcessInstance.getProcessDefinitionName();
        this.businessStartTime = historicProcessInstance.getStartTime();
        this.taskName = historicTaskInstance.getName();
        this.taskStartTime = historicTaskInstance.getStartTime();
        this.claimTime = historicTaskInstance.getClaimTime();
        this.taskId = historicTaskInstance.getId();
    }
    
    /**
     * 设置个人已办列表的返回数据
     * 
     * @param historicProcessInstance
     * @param task
     * @param historicTaskInstance
     * @Description:
     */
    public void setPersonalDoneWorkAttributes(HistoricProcessInstance historicProcessInstance,
        HistoricTaskInstance historicTaskInstance) {
        this.setHanglingWorkAttributes(historicProcessInstance, historicTaskInstance);
        this.durationTime = this.millisecondsToDate(historicTaskInstance.getDurationInMillis());
        this.taskEndTime = historicTaskInstance.getEndTime();
    }
    
    /**
     * 设置办结业务列表的返回数据
     * 
     * @param historicProcessInstance
     * @Description:
     */
    public void setFinishedWorkAttributes(HistoricProcessInstance historicProcessInstance) {
        this.businessAcceptNumber = historicProcessInstance.getId();
        this.businessName = historicProcessInstance.getProcessDefinitionName();
        this.businessStartTime = historicProcessInstance.getStartTime();
        this.businessEndTime = historicProcessInstance.getEndTime();
        this.durationTime = this.millisecondsToDate(historicProcessInstance.getDurationInMillis());
    }
    
    /**
     * 将毫秒数转为X天X小时X分钟
     * 
     * @param millisecond
     * @return
     * @Description:
     */
    private String millisecondsToDate(Long millisecond) {
        String date = "";
        long day = millisecond / 86400000;
        long hour = (millisecond % 86400000) / 3600000;
        long minute = (millisecond % 86400000 % 3600000) / 60000;
        if (day > 0) {
            date = String.valueOf(day) + "天";
        }
        if (hour > 0) {
            date += String.valueOf(hour) + "小时";
        }
        if (minute > 0) {
            date += String.valueOf(minute) + "分钟";
        }
        return date;
    }
    
}
