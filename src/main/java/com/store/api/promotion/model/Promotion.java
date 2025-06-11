package com.store.api.promotion.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Promotion {
    private String name;
    private int buy;
    private int get;
    private Long startDate;
    private Long endDate;
}
