package com.southsmart.webplat.workflow.activiti.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.southsmart.webplat.workflow.activiti.exporer.JsonpCallbackFilter;

@Configuration
@ComponentScan({"org.activiti.rest", "org.activiti.conf"})
@EnableAsync
public class ActivitiConfig {
    
    @Bean
    public JsonpCallbackFilter filter() {
        return new JsonpCallbackFilter();
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }
}
