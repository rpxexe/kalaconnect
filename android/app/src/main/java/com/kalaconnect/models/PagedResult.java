package com.kalaconnect.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PagedResult<T> implements Serializable {

    @SerializedName("items")
    private List<T> items = new ArrayList<>();

    @SerializedName("page")
    private int page = 0;

    @SerializedName("size")
    private int size = 20;

    @SerializedName("totalItems")
    private long totalItems = 0;

    @SerializedName("totalPages")
    private int totalPages = 0;

    @SerializedName("hasNext")
    private boolean hasNext = false;

    public PagedResult() {
    }

    public List<T> getItems() {
        return items != null ? items : new ArrayList<>();
    }

    public void setItems(List<T> items) {
        this.items = items;
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
