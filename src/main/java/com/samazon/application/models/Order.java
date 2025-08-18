package com.samazon.application.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.samazon.application.models.enums.OrderStatus;
import com.samazon.application.models.records.AddressRecord;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "orders")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double totalAmount;
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "addressLine1", column = @Column(name = "shipping_address_line1")),
            @AttributeOverride(name = "addressLine2", column = @Column(name = "shipping_address_line2")),
            @AttributeOverride(name = "city", column = @Column(name = "shipping_city")),
            @AttributeOverride(name = "state", column = @Column(name = "shipping_state")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "shipping_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "shipping_country")),
    })
    private AddressRecord shippingAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "addressLine1", column = @Column(name = "billing_address_line1")),
            @AttributeOverride(name = "addressLine2", column = @Column(name = "billing_address_line2")),
            @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
            @AttributeOverride(name = "state", column = @Column(name = "billing_state")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "billing_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "billing_country")),
    })
    private AddressRecord billingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @OneToMany(mappedBy = "order", cascade = { CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REMOVE }, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    @ToString.Exclude
    private Payment payment;
}
