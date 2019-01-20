package com.zhou.utils.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParamUtils {
    
    // 支持json
    private static ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    /**
     * 对象转换成Json字符串
     * 
     * @param obj
     * @return
     * @throws Exception
     */
    public static String obj2Str(Object obj)
        throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
    
    /**
     * Json字符串转换成Map<String, ?>
     * 
     * @param <T>
     * @param jsonStr
     * @return
     * @throws Exception
     */
    public static Map<String, Object> json2map(String jsonStr)
        throws Exception {
        return objectMapper.readValue(jsonStr, new TypeReference<Map<String, Object>>() {
        });
    }
    
    /**
     * Json字符串转换成T
     * 
     * @param <T>
     * @param jsonStr
     * @param clazz
     * @return
     * @throws Exception
     */
    public static <T> T json2obj(String jsonStr, Class<T> clazz)
        throws Exception {
        return objectMapper.readValue(jsonStr, clazz);
    }
    
    /**
     * Map转换成T
     * 
     * @param map
     * @param clazz
     * @return
     * @throws Exception
     */
    public static <T> T map2obj(Map<String, Object> map, Class<T> clazz)
        throws Exception {
        String jsonStr = obj2Str(map);
        return objectMapper.readValue(jsonStr, clazz);
    }
    
    /**
     * 对象转换成Map
     * 
     * @param <T>
     * @param jsonStr
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Map<String, Object> obj2Map(Object obj)
        throws Exception {
        String jsonStr = obj2Str(obj);
        return json2map(jsonStr);
    }
    
    public static <T> List<T> str2List(String str, Class<?> collectionClass, Class<T> elementClasses)
        throws Exception {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        return objectMapper.readValue(str, javaType);
    }
    
    /**
     * 获取请求参数，需要完善，支持get方法，post方法（headers 的 Content-Type 为
     * application/x-www-form-urlencoded和application/json）。
     * 
     * @param request
     * @return
     * @throws Exception
     */
    public static Map<String, Object> getParamMap(HttpServletRequest request)
        throws Exception {
        String methodName = request.getMethod();
        if ("POST".equalsIgnoreCase(methodName)) {
            String contentType = request.getHeader("Content-Type");
            if (contentType != null && contentType.indexOf("application/json") >= 0) {//post方式参json
                String jsonStr = parseJsonParams(request);
                return json2map(jsonStr);
            }
        }
        Map<String, String[]> paramMap = request.getParameterMap();
        return convertParamMap(paramMap);
    }
    
    /**
     * 转换参数格式
     * 
     * @param paramMap
     * @return
     */
    public static Map<String, Object> convertParamMap(Map<String, String[]> paramMap) {
        Map<String, Object> result = new HashMap<>();
        Set<Entry<String, String[]>> entryset = paramMap.entrySet();
        if (entryset.size() > 0) {
            for (Entry<String, String[]> entry : entryset) {
                String key = entry.getKey();
                String[] valueArr = entry.getValue();
                String value = valueArr.length > 0 ? valueArr[0] : null;
                result.put(key, value);
            }
        }
        return result;
    }
    
    /**
     * 解析request中的json格式的参数
     * 
     * @param request
     * @return
     * @throws IOException
     */
    public static String parseJsonParams(HttpServletRequest request)
        throws IOException {
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);
        return responseStrBuilder.toString();
    }
    
    /**
     * 判定map是否为空
     * 
     * @param map
     * @return
     */
    public static boolean mapIsEmpty(Map<?, ?> map) {
        if (map == null || map.size() == 0) {
            return true;
        }
        return false;
    }
    
    /**
     * 获取int数据
     * 
     * @param key
     * @param paramMap
     * @return
     */
    public static Integer getIntParam(String key, Map<String, Object> paramMap) {
        if (!mapIsEmpty(paramMap)) {
            Object value = paramMap.get(key);
            if (value == null) {
                return null;
            }
            return Integer.parseInt(value.toString());
        } else {
            return null;
        }
    }
    
    /**
     * 获取double型数据
     * 
     * @param key
     * @param paramMap
     * @return
     */
    public static Double getDoubleParam(String key, Map<String, Object> paramMap) {
        if (!mapIsEmpty(paramMap)) {
            Object value = paramMap.get(key);
            if (value == null) {
                return null;
            }
            return Double.parseDouble(value.toString());
        } else {
            return null;
        }
    }
    
    /**
     * 获取浮点型数据
     * 
     * @param key
     * @param paramMap
     * @return
     */
    public static Float getFloatParam(String key, Map<String, Object> paramMap) {
        if (!mapIsEmpty(paramMap)) {
            Object value = paramMap.get(key);
            if (value == null) {
                return null;
            }
            return Float.parseFloat(value.toString());
        } else {
            return null;
        }
    }
    
    /**
     * 获取字符串
     * 
     * @param key
     * @param paramMap
     * @return
     * @throws Exception
     */
    public static String getStringParam(String key, Map<String, Object> paramMap)
        throws Exception {
        if (!mapIsEmpty(paramMap)) {
            Object value = paramMap.get(key);
            if (value == null) {
                return null;
            }
            return value.toString();
        } else {
            return null;
        }
    }
}
