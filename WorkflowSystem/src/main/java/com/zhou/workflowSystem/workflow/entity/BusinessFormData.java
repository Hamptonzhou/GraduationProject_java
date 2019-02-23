package com.zhou.workflowSystem.workflow.entity;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * 存放表单数据
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年2月23日
 * @Version:1.1.0
 */
//@Entity
//@Table(name = "business_form_data")
//@Data
//@DynamicUpdate
public class BusinessFormData {
    
    //等于流程实例的Business_Key，建立起某个流程与某个表单的关系
    @Id
    @Column(name = "business_key")
    private String businessKey;
    
    //保存该字段时。使用富文本过滤
    @Column(name = "form_data", length = 4000)
    private String formData;
    
}
