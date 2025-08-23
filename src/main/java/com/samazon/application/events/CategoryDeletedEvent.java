package com.samazon.application.events;

import java.util.Set;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class CategoryDeletedEvent extends ApplicationEvent {
    private final Set<Long> cartIds;

    public CategoryDeletedEvent(Object source, Set<Long> cartIds) {
        super(source);
        this.cartIds = cartIds;
    }
}
