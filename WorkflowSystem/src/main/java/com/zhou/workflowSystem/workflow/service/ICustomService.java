package com.zhou.workflowSystem.workflow.service;

import java.io.IOException;

import com.zhou.utils.PageQueryData;
import com.zhou.workflowSystem.workflow.entity.MyWorkEntity;
import com.zhou.workflowSystem.workflow.entity.ProcessDefinitionTree;

/**
 * 自定义查询接口声明
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年2月6日
 * @Version:1.1.0
 */
public interface ICustomService<T> {
    
    /**
     * 获取流程定义树
     * 
     * @return
     * @Description:
     */
    ProcessDefinitionTree getProcessDefinitionTree();
    
    /**
     * 获取在办工作、个人已办、办结工作列表
     * 
     * @param pageQueryData 传递用户的真实姓名到queryId中，并且在searchText中指定查询数据的类型
     * @param request
     * @return
     * @throws Exception
     * @Description:searchText取值为：HanglingWork、FinishedWork、PersonalDoneWork
     */
    void getMyWorkListBysearchText(PageQueryData<T> pageQueryData);
    
    /**
     * 获取流程实例的状态图片，正在执行的环节会有红色边框
     * 
     * @param pageQueryData
     * @throws IOException
     * @Description:
     */
    void getProcessStatusImage(PageQueryData<MyWorkEntity> pageQueryData)
        throws IOException;
    
    /**
     * 接办任务
     * 
     * @param taskId
     * @param userId
     * @Description:当userId为空时，执行退签功能。退签之后，组成员都可以查看任务内容。回退到组任务的前提是，本来是一个组任务
     */
    void claimTask(String taskId, String userId);
    
    /**
     * 完成任务
     * 
     * @param taskId
     * @param variables
     * @Description:
     */
    void completeTask(String taskId);
    
    /**
     * 在流程变量中， 设置备注内容
     * 
     * @param taskId
     * @param variables
     * @Description:
     */
    void setRemarkContent(String taskId, String remarkContent);
    
}
