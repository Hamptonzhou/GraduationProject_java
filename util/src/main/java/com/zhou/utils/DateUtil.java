package com.zhou.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期处理工具类
 * 
 * @author hgc
 *
 */
public final class DateUtil {
    
    /**
     * 长日期格式（日期+时间）
     */
    public static final String FULL_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 短日期格式（无时间）
     */
    public static final String ONLY_DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * 仅时间格式
     */
    public static final String ONLY_TIME_FORMAT = "HH:mm:ss";
    
    /**
     * 转换成标准日期格式字符串
     * 
     * @param dtv
     * @return
     */
    public static String dateToString(Date dtv) {
        return dateToString(dtv, false);
    }
    
    public static String dateToString(Date dtv, String sFmt) {
        if (!sFmt.equals("needed")) {
            SimpleDateFormat sdf = new SimpleDateFormat(sFmt);
            return sdf.format(dtv);
        } else {
            return dtv.toString();
        }
        
    }
    
    /**
     * 转换成标准日期格式字符串
     * 
     * @param dtv
     * @param bOnlyDate 是否仅转换日期部分（不会返回时间部分）。
     * @return
     */
    public static String dateToString(Date dtv, boolean bOnlyDate) {
        if (dtv == null)
            return "";
        
        SimpleDateFormat sdf = new SimpleDateFormat(bOnlyDate ? ONLY_DATE_FORMAT : FULL_DATETIME_FORMAT);
        return sdf.format(dtv);
    }
    
    /**
     * 将Date对象转换成Calendar对象。如果date为null，则返回当前时间
     * 
     * @param date
     * @return
     */
    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        if (date != null)
            cal.setTime(date);
        return cal;
    }
}
