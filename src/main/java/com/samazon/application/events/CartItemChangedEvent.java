package com.samazon.application.events;

import org.springframework.context.ApplicationEvent;

public class CartItemChangedEvent extends ApplicationEvent {
    private final Long cartId;

    public CartItemChangedEvent(Object source, Long cartId) {
        super(source);
        this.cartId = cartId;
    }

    public Long getCartId() {
        return cartId;
    }
}
