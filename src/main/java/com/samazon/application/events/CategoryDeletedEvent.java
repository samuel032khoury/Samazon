package com.samazon.application.events;

import org.springframework.context.ApplicationEvent;

public class CategoryDeletedEvent extends ApplicationEvent {
    private final Long categoryId;

    public CategoryDeletedEvent(Object source, Long categoryId) {
        super(source);
        this.categoryId = categoryId;
    }

    public Long getCategoryId() {
        return categoryId;
    }
}
