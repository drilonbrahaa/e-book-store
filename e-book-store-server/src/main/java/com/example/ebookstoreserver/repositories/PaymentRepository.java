package com.example.ebookstoreserver.repositories;

import com.example.ebookstoreserver.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentIntentId(
            String paymentIntentId
    );
}

