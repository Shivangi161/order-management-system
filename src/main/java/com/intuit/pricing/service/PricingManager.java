package com.intuit.pricing.service;

import org.springframework.http.ResponseEntity;

public interface PricingManager {
    ResponseEntity getPrice(Long productId);
}
