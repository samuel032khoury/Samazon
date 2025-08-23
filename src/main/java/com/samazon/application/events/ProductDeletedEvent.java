package com.samazon.application.events;

import java.util.List;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class ProductDeletedEvent extends ApplicationEvent {
    private final List<Long> cartIds;

    public ProductDeletedEvent(Object source, List<Long> cartIds) {
        super(source);
        this.cartIds = cartIds;
    }
}
