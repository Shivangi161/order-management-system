package com.intuit.pricing.controller;


import com.intuit.pricing.service.PricingManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/pricing")
public class PricingController {

    private PricingManager pricingManager;
    public PricingController(PricingManager pricingManager) {
        this.pricingManager = pricingManager;
    }

    @GetMapping(value = "/quote/{productId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity fetchPrice(@PathVariable("productId") Long productId) {
         return pricingManager.getPrice(productId);
    }

   /* @GetMapping(value = "/quote/", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity fetchPrice(@PathVariable("productId") Long productId) {*///TODO Shivangi fetch price for multiple products at a time
      /*  return pricingManager.getPrice(productId);
    }*/

}
