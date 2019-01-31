package com.southsmart.webplat.common.util;

import com.southsmart.webplat.common.constant.Constants;
import com.southsmart.webplat.common.model.Result;

/**
 * 统一返回格式 工具类
 */
public class ResultUtil {
    
    //成功，有返回数据
    public static Result success(Object object) {
        Result result = new Result();
        result.setStatus(Constants.RESULT_STATUS_SUCCESS);
        result.setMessage(Constants.RESULT_MESSAGE_SUCCESS);
        result.setData(object);
        return result;
    }
    
    //成功，无返回数据，如插入新增 修改等
    public static Result success() {
        return success(null);
    }
    
    //程序出错
    public static Result error() {
        Result result = new Result();
        result.setStatus(Constants.RESULT_STATUS_ERROR);
        result.setMessage(Constants.RESULT_MESSAGE_ERROR);
        return result;
    }
    
    //程序出错
    public static Result error(Object object) {
        Result result = new Result();
        result.setStatus(Constants.RESULT_STATUS_ERROR);
        result.setMessage(Constants.RESULT_MESSAGE_ERROR);
        result.setData(object);
        return result;
    }
    
    //程序出错
    public static Result error(String msg, Object object) {
        Result result = new Result();
        result.setStatus(Constants.RESULT_STATUS_ERROR);
        result.setMessage(msg);
        result.setData(object);
        return result;
    }
    
    //失败 自定义说明
    public static Result fail(String msg, Object object) {
        Result result = new Result();
        result.setStatus(Constants.RESULT_STATUS_FAIL);
        result.setMessage(msg);
        result.setData(object);
        return result;
    }
    
    //失败 自定义说明
    public static Result fail(String msg) {
        Result result = new Result();
        result.setStatus(Constants.RESULT_STATUS_FAIL);
        result.setMessage(msg);
        return result;
    }
    
    //失败
    public static Result fail() {
        Result result = new Result();
        result.setStatus(Constants.RESULT_STATUS_FAIL);
        result.setMessage(Constants.RESULT_MESSAGE_FAIL);
        return result;
    }
    
}
