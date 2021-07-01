package com.intuit.ordermanagementservice.service;

import com.intuit.ordermanagementservice.DTOs.ProductDTOs;
import com.intuit.ordermanagementservice.enums.PaymentStatus;
import org.springframework.http.ResponseEntity;

public interface OrderManager {
    ResponseEntity createOrder(ProductDTOs productDTOs, String uidx);
    ResponseEntity updatePaymentStatusForOrder(PaymentStatus paymentStatus, Long orderId);
}
