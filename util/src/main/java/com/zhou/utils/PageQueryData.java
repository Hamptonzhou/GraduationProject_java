package com.zhou.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询实体类的扩展
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月17日
 * @Version:1.1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PageQueryData<T> extends PageEntity<T> {
    
    /**
     * 传递需要查询的id
     */
    private String queryId;
    
    /**
     * 传递需要查询的内容
     */
    private String searchText;
    
    /**
     * 按照某个属性排序
     */
    private Map<String, Boolean> sort = new LinkedHashMap<>();
    
    /**
     * 传递多个需要查询的参数
     */
    private Map<String, String> searchTextMap;
    
    /**
     * 获取已设置的排序字段（有先后顺序）
     * 
     * @return
     */
    public Map<String, Boolean> getSort() {
        return sort;
    }
    
    /**
     * 添加一个排序字段
     * 
     * @param field 字段名
     * @param asc 是否升序
     */
    public void addSortField(String field, boolean asc) {
        sort.put(field, asc);
    }
    
}
