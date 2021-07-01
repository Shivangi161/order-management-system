package com.intuit.ordermanagementservice.enums;

public enum OrderStatus {

    PENDING,//Depends on whether we want to cancel the payment on non-placement or place it with COD
    PLACED,
    CANCELLED,
    OUT_FOR_DELIVERY,
    DELIVERED


}
