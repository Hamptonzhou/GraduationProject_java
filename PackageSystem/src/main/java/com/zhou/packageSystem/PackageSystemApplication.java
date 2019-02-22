package com.zhou.packageSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.zhou"})
@EnableJpaRepositories(basePackages = {"com.zhou.*.dao","com.zhou.workflowSystem.workflow.dao"})
//@EntityScan(basePackages = "com.zhou.*.entity")
public class PackageSystemApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PackageSystemApplication.class, args);
    }
}
