package com.zhou.globalconfig;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zhou.utils.common.model.Result;
import com.zhou.utils.common.util.ExceptionUtils;
import com.zhou.utils.common.util.ResultUtil;

/**
 * 全局异常捕获，需要扫描改路径下的类
 * 
 * @Title:
 * @Description:
 * @Author:Administrator
 * @Since:2018年10月26日
 * @Version:1.1.0
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    /**
     * 所有异常报错
     * 
     * @param request
     * @param exception
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    public Result allExceptionHandler(HttpServletRequest request, Exception exception)
        throws Exception {
        exception.printStackTrace();
        String errorMsg = exception.getLocalizedMessage();
        if (errorMsg == null)
            errorMsg = "";
        if (errorMsg.startsWith("[errs]")) {
            errorMsg = errorMsg.substring(6, errorMsg.length() - 6);
        }
        return ResultUtil.error(errorMsg, ExceptionUtils.printExceptionDetail(exception));
    }
}
