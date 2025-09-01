package com.samazon.application.models;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("STRIPE")
public class StripePayment extends Payment {
    @Column(name = "stripe_payment_intent_id", unique = true)
    private String stripePaymentIntentId;

    @Column(name = "stripe_metadata", columnDefinition = "TEXT")
    private String stripeMetadata; // JSON

    @Override
    public String getProviderPaymentId() {
        return stripePaymentIntentId;
    }

    @Override
    public Map<String, String> getProviderPayloads() {
        if (stripeMetadata == null)
            return new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(stripeMetadata, new TypeReference<Map<String, String>>() {
            });
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
