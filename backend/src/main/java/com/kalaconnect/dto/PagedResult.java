package com.kalaconnect.dto;

import java.util.ArrayList;
import java.util.List;

public class PagedResult<T> {

    private List<T> items = new ArrayList<>();
    private int page = 0;
    private int size = 20;
    private long totalItems = 0;
    private int totalPages = 0;
    private boolean hasNext = false;

    public PagedResult() {
    }

    public PagedResult(List<T> items, int page, int size, long totalItems) {
        this.items = items != null ? items : new ArrayList<>();
        this.page = page;
        this.size = size > 0 ? size : 20;
        this.totalItems = totalItems;
        this.totalPages = (int) Math.ceil((double) totalItems / this.size);
        this.hasNext = page < this.totalPages - 1;
    }

    public static <T> PagedResult<T> of(List<T> items, int page, int size, long totalItems) {
        return new PagedResult<>(items, page, size, totalItems);
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
}
