package com.charity.common;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private Long total;
    private List<T> items;

    public PageResult(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }
}
