package com.intuit.ordermanagementservice.controller;


import com.intuit.ordermanagementservice.DTOs.PaymentStatusUpdateDTO;
import com.intuit.ordermanagementservice.DTOs.ProductDTOs;
import com.intuit.ordermanagementservice.service.OrderManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/order")
public class OrderController {

    private OrderManager orderManager;

    public OrderController(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity createOrder(@Valid @RequestBody ProductDTOs productDTOs,@RequestHeader("user") String uidx) {
        return orderManager.createOrder(productDTOs,uidx);
    }

    @PatchMapping(value = "/payment-status", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity updateOrderPaymentStatus(@Valid @RequestBody PaymentStatusUpdateDTO paymentStatusUpdateDTO) {
        return orderManager.updatePaymentStatusForOrder(paymentStatusUpdateDTO.getPaymentStatus(), paymentStatusUpdateDTO.getOrderId());
    }

}
