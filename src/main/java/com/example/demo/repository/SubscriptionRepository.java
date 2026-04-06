package com.example.demo.repository;

import com.example.demo.model.Scadenza;
import com.example.demo.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    Subscription findByScadenza(Scadenza scadenza);
}