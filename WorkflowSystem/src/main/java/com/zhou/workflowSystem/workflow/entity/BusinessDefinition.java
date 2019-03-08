package com.zhou.workflowSystem.workflow.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

@Entity
@Table(name = "business_definition")
@Data
@DynamicUpdate
public class BusinessDefinition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String businessName;
    
    //业务使用的流程
    private String processDefinitionId;
    
    //业务使用的表单
    private String businessFormId;
    
    //备注信息，启动之后获得流程实例进行设置
    private Object remarkContent;
    
}
