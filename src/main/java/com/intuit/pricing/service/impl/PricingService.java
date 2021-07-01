package com.intuit.pricing.service.impl;

import com.intuit.pricing.service.PricingManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PricingService implements PricingManager {


    @Override
    public ResponseEntity getPrice(Long productId) {
        try {
            if (productId == null) {
                throw new IllegalArgumentException("Wrong productId");
            }
            if (productId == 1) {
                return ResponseEntity.status(HttpStatus.OK).body(1000d);
            }

            return ResponseEntity.status(HttpStatus.OK).body(2000d);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
