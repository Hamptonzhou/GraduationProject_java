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

    @Column(length = 50, nullable = false)
    private String businessName;

    //业务使用的流程
    @Column(length = 50, nullable = false)
    private String processDefinitionId;

    @Column(length = 50, nullable = false)
    private String processDefinitionName;

    //业务使用的表单
    @Column(length = 50, nullable = false)
    private String businessFormId;

    @Column(length = 50, nullable = false)
    private String businessFormName;

    //备注信息，启动之后获得流程实例进行设置
    private String remarkContent;

    //业务的创建用户Id
    private String creatorId;

    //业务是否启动
    private boolean started;


}
