package com.seeingvoice.www.svhearing.javabeans.userDao;

/**
 * Date:2019/6/11
 * Time:13:54
 * auther:zyy
 */
public class page {
    private Long item_count;
    private Long page_size;
    private Long page_count;
    private Long page_index;
    private Long offset;
    private Long limit;
    private Boolean has_next;
    private Boolean has_previous;

    public page() {
    }

    public page(Long item_count, Long page_size, Long page_count, Long page_index, Long offset, Long limit, Boolean has_next, Boolean has_previous) {
        this.item_count = item_count;
        this.page_size = page_size;
        this.page_count = page_count;
        this.page_index = page_index;
        this.offset = offset;
        this.limit = limit;
        this.has_next = has_next;
        this.has_previous = has_previous;
    }
    public Long getItem_count() {
        return item_count;
    }

    public void setItem_count(Long item_count) {
        this.item_count = item_count;
    }

    public Long getPage_size() {
        return page_size;
    }

    public void setPage_size(Long page_size) {
        this.page_size = page_size;
    }

    public Long getPage_count() {
        return page_count;
    }

    public void setPage_count(Long page_count) {
        this.page_count = page_count;
    }

    public Long getPage_index() {
        return page_index;
    }

    public void setPage_index(Long page_index) {
        this.page_index = page_index;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Boolean getHas_next() {
        return has_next;
    }

    public void setHas_next(Boolean has_next) {
        this.has_next = has_next;
    }

    public Boolean getHas_previous() {
        return has_previous;
    }

    public void setHas_previous(Boolean has_previous) {
        this.has_previous = has_previous;
    }
}
