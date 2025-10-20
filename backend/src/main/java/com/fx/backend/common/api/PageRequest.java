package com.fx.backend.common.api;

public class PageRequest {
    private int pageNo = 1;
    private int pageSize = 10;

    public int getPageNo() { return pageNo; }
    public void setPageNo(int pageNo) { this.pageNo = Math.max(pageNo, 1); }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = Math.max(Math.min(pageSize, 100), 1); }

    public int offset() { return (pageNo - 1) * pageSize; }
}


