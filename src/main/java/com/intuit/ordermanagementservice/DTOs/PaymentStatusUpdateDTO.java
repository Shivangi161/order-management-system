package com.intuit.ordermanagementservice.DTOs;

import com.intuit.ordermanagementservice.enums.PaymentStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PaymentStatusUpdateDTO {
    @NotNull
    private Long orderId;
    @NotNull
    private PaymentStatus paymentStatus;
}
