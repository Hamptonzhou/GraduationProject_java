package com.zhou.workflowSystem.workflow.activiti.config;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zhou.workflowSystem.workflow.listener.GlobalEventListener;

@Component
public class ShareniuProcessEngineConfigurationConfigurer implements ProcessEngineConfigurationConfigurer {
    
    @Autowired
    private GlobalEventListener globalEventListener;
    
    @Override
    public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
        processEngineConfiguration.setActivityFontName("宋体");
        processEngineConfiguration.setLabelFontName("宋体");
        processEngineConfiguration.setAnnotationFontName("宋体");
        //配置全局监听器
        List<ActivitiEventListener> activitiEventListener = new ArrayList<>();
        activitiEventListener.add(globalEventListener);
        processEngineConfiguration.setEventListeners(activitiEventListener);
    }
    
}
