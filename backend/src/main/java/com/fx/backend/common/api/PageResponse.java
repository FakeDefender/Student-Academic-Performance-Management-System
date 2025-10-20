package com.fx.backend.common.api;

import java.util.List;

public class PageResponse<T> {
    private long total;
    private int pageNo;
    private int pageSize;
    private List<T> records;

    public static <T> PageResponse<T> of(long total, int pageNo, int pageSize, List<T> records) {
        PageResponse<T> p = new PageResponse<>();
        p.total = total;
        p.pageNo = pageNo;
        p.pageSize = pageSize;
        p.records = records;
        return p;
    }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getPageNo() { return pageNo; }
    public void setPageNo(int pageNo) { this.pageNo = pageNo; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public List<T> getRecords() { return records; }
    public void setRecords(List<T> records) { this.records = records; }
}


