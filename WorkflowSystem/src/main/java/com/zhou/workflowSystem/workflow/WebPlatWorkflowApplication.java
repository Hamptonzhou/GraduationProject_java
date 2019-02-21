package com.zhou.workflowSystem.workflow;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年2月2日
 * @Version:1.1.0
 */
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@ComponentScan(basePackages = {"com.zhou"})
public class WebPlatWorkflowApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(WebPlatWorkflowApplication.class, args);
    }
}
