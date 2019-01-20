package com.zhou.aspect;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zhou.entity.LogInfo;
import com.zhou.service.ILogService;
import com.zhou.utils.OutputLogParameter;
import com.zhou.utils.webservice.WebServiceUtils;

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
    
    @Autowired
    private ILogService logService;
    
    /**
     * 注解@RequestMapping的切点
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestAnnotationPointCut() {
    }
    
    @Around("requestAnnotationPointCut()")
    public Object pointcutControllerAround(ProceedingJoinPoint joinPoint)
        throws Throwable {
        //定义返回对象、得到方法需要的参数  
        Object returnObject = null;
        Object[] args = joinPoint.getArgs();
        long startTime = System.currentTimeMillis();
        //放行
        returnObject = joinPoint.proceed(args);
        //写入日志信息到mongodb
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
        String outArgs = parseArgs(args);
        String userId = WebServiceUtils.getCurrentUserId();
        String client = WebServiceUtils.getRequest().getRemoteAddr();
        String requestUri = WebServiceUtils.getRequest().getRequestURL().toString();
        String description = "请求路径 ：" + requestUri;
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        LogInfo logInfo = new LogInfo(methodName, outArgs, userId, client, requestUri, executionTime, description);
        logService.writeInvoke(logInfo);
        return returnObject;
    }
    
    private String parseArgs(Object[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            Object object = args[i];
            //HttpServletRequest,HttpServletResponse不输出
            if (object instanceof HttpServletRequest || object instanceof HttpServletResponse)
                continue;
            //反序列化obj的属性到相应参数中.
            sb.append(object == null ? "null," : OutputLogParameter.printFields(object, "", 0) + ",");
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
}
