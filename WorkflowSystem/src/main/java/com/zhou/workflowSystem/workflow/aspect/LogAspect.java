package com.zhou.workflowSystem.workflow.aspect;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 切面类，配置切点和切面
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月15日
 * @Version:1.1.0
 */
@Aspect
@Component
public class LogAspect {
    
    /**
     * 注解@RequestMapping的切点
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestAnnotationPointCut() {
    }
    
    @Before("requestAnnotationPointCut()")
    public void pointcutControllerAround(JoinPoint joinPoint)
        throws Throwable {
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println(date + "---访问的controller方法：" + methodName);
    }
}
