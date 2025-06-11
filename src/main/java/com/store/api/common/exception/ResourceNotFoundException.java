package com.store.api.common.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends BaseException {
    private final String resourceType;
    private final Object identifier;

    public ResourceNotFoundException(String resourceType, Object identifier) {
        super(resourceType + " not found with identifier: " + identifier);
        this.resourceType = resourceType;
        this.identifier = identifier;
    }
}
