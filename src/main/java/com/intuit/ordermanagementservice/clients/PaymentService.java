package com.intuit.ordermanagementservice.clients;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    static int counter = 1;

    private PaymentsInternalService paymentsInternalService;

    public PaymentService(PaymentsInternalService paymentsInternalService) {
        this.paymentsInternalService = paymentsInternalService;
    }

    public boolean pay(Long orderId, Double amountToBePaid) {
        if (orderId == null || amountToBePaid == null) {
            throw new IllegalArgumentException("wrong inputs");
        }
        if (counter % 20 == 0) { //if payment service is unavailable
            counter++;
            return false;
        }
        counter++;
        //Acknowledging the request has been received and processing it in an async manner
        paymentsInternalService.pay(orderId, amountToBePaid);
        LOGGER.info("Ack from payments for order id : {}", orderId);
        return true;
    }

}
