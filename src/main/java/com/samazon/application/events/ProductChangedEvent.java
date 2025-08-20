package com.samazon.application.events;

import org.springframework.context.ApplicationEvent;

public class ProductChangedEvent extends ApplicationEvent {
    private final Long productId;

    public ProductChangedEvent(Object source, Long productId) {
        super(source);
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
}
