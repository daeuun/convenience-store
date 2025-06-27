package com.store.api.product.model;

public record ProductSearchParam(
        String name, Long lastId, int limit
) {
}
