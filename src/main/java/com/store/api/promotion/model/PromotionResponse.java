package com.store.api.promotion.model;

import java.time.LocalDateTime;

public record PromotionResponse(String name, int buy, int get, LocalDateTime startDate, LocalDateTime endDate) {

}
