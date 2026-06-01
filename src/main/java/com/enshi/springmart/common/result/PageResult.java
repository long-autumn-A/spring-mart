package com.enshi.springmart.common.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 通用分页返回对象，前端拿到统一格式
 */
@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 当前页数据 */
    private List<T> records;

    /** 总条数 */
    private long total;

    /** 当前页码 */
    private long page;

    /** 每页条数 */
    private long pageSize;

    /** 总页数 */
    private long totalPages;

    public PageResult(List<T> records, long total, long page, long pageSize) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = pageSize == 0 ? 0 : (total + pageSize - 1) / pageSize;
    }
}
