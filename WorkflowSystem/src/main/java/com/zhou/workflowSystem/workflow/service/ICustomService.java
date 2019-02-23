package com.zhou.workflowSystem.workflow.service;

import java.util.List;

import com.zhou.utils.PageQueryData;
import com.zhou.workflowSystem.workflow.entity.Leave;
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
    
}
