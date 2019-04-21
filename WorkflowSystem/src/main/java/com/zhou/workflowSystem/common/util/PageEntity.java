package com.zhou.workflowSystem.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.Data;

/**
 * 分页查询实体，包括：page、rows、total、queryList属性
 * 
 * @Title:
 * @Description:
 * @Author:zhou
 * @Since:2019年1月15日
 * @Version:1.1.0
 */
@Data
public class PageEntity<T> {
    
    /**
     * 当前页面 ，基于1开始
     */
    private int page = 1;
    
    /**
     * 每页记录数，默认值为20
     */
    private int rows = 20;
    
    /**
     * 总记录数
     */
    private int total;
    
    /**
     * 
     * 存放查询数据库后返回的结果集，是一个List类型
     */
    private List<T> queryList;
    
    public void setPage(int page) {
        this.page = (page <= 0) ? 1 : this.page;
    }
    
    /**
     * 获取总记录数
     * 
     * @return
     */
    public int getTotal() {
        if (total <= 0) {
            if (queryList != null)
                total = queryList.size();
        }
        return total;
    }
    
    /**
     * 返回组装的结果
     * 
     * @return
     */
    public Map<String, Object> getResult() {
        return PageEntity.getResult(this.getTotal(), this.getQueryList());
    }
    
    /**
     * 返回组装的结果
     * 
     * @param iSize 返回结果总记录数，如果为负值，则从rows获取
     * @param rows 当前页的内容
     * @return
     */
    public static Map<String, Object> getResult(int total, List<?> rows) {
        Map<String, Object> result = new HashMap<String, Object>(2);
        if (rows == null) {
            rows = new ArrayList<Object>(1);
        }
        if (total < 0) {
            total = rows.size();
        }
        result.put("total", total);
        result.put("rows", rows);
        return result;
    }
    
    /**
     * 根据pageQueryData中的页数、每页显示的条数设置并返回分页对象
     * 
     * @param <T>
     * @param pageQueryData
     * @return
     * @Description:
     */
    public Pageable getPageable() {
        int page = (this.page <= 0) ? 1 : this.page;
        int rows = this.rows;
        //zero-based page index.
        Pageable pageable = PageRequest.of(page - 1, rows);
        return pageable;
    }
}
