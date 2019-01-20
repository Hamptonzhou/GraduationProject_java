package com.zhou.utils.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 项目里常用的一些工具类
 * 
 * @Title:
 * @Description:
 * @Author:hgc
 * @Since:2018年6月4日
 */
public class ExceptionUtils {
    
    /**
     * 获取异常的堆栈信息，长度最长为1800
     * 
     * @param e
     * @return
     */
    public static String printExceptionDetail(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String print = sw.toString();
        int len = print.length();
        return len > 1800 ? print.substring(0, 1800) : print.substring(0, len);
    }
    
}
