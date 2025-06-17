package com.store.api.promotion.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Getter
@NoArgsConstructor
@Alias("Promotion")
public class Promotion {
    private Long id;
    private String name;
    private int buy;
    private int get;
    private Long startDate;
    private Long endDate;

    public Promotion(Long id, String name, int buy, int get, Long startDate, Long endDate) {
        this.id = id;
        this.name = name;
        this.buy = buy;
        this.get = get;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
