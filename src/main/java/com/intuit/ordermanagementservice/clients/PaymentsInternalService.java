package com.intuit.ordermanagementservice.clients;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PaymentsInternalService {

    @Async
    public void pay(Long orderId, Double amountToBePaid){
        //Internal implementation
    }
}
