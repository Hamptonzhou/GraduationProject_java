package com.zhou.workflowSystem.workflow.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

/**
 * 存放表单数据
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年2月23日
 * @Version:1.1.0
 */
@Entity
@Table(name = "business_form_data")
@Data
@DynamicUpdate
public class BusinessFormData {
    
    //等于流程实例的Business_Key，建立起某个流程与某个表单的关系
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    @Column(name = "form_name", length = 50)
    private String formName;
    
    //保存该字段时。使用富文本过滤
    @Column(name = "form_data")
    private String formData;
    
}
