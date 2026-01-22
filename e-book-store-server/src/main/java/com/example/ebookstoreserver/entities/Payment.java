package com.example.ebookstoreserver.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment extends BaseEntity {

    @OneToOne(optional = false)
    private Order order;

    private String provider; // STRIPE

    private String paymentIntentId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public enum PaymentStatus {
        INITIATED, SUCCEEDED, FAILED
    }
}

