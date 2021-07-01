package com.intuit.ordermanagementservice.service.impl;

import com.intuit.ordermanagementservice.DTOs.ProductDTOs;
import com.intuit.ordermanagementservice.clients.PaymentService;
import com.intuit.ordermanagementservice.dao.OrderDAO;
import com.intuit.ordermanagementservice.enitities.Order;
import com.intuit.ordermanagementservice.enitities.OrderItem;
import com.intuit.ordermanagementservice.enums.PaymentStatus;
import com.intuit.ordermanagementservice.exception.OrderNotPlacedException;
import com.intuit.ordermanagementservice.exception.PriceChangedException;
import com.intuit.ordermanagementservice.exception.PriceNotFetchedException;
import com.intuit.ordermanagementservice.service.OrderManager;
import com.intuit.ordermanagementservice.helper.OrderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class OrderService implements OrderManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    private PaymentService paymentService;
    private OrderDAO orderDAO;
    private PriceValidatorService priceValidatorService;

    public OrderService(PaymentService paymentService, OrderDAO orderDAO, PriceValidatorService priceValidatorService) {
        this.paymentService = paymentService;
        this.orderDAO = orderDAO;
        this.priceValidatorService = priceValidatorService;
    }

    @Override
    public ResponseEntity createOrder(ProductDTOs productDTOs, String uidx) {
        try {
            if (productDTOs == null) {
                LOGGER.error("ProductDTOs is null");
                throw new IllegalArgumentException("ProductDTOs can't be null");
            }

            priceValidatorService.validatePriceForProducts(productDTOs);

            List<OrderItem> orderItems = OrderHelper.translateProductsToOrderItems(productDTOs.getProductDTOList());
            double totalPrice = 0d;
            for (OrderItem orderItem : orderItems) {
                totalPrice = Double.sum(totalPrice, orderItem.getDiscountedPrice());
            }
            LOGGER.info("The total Price is {} and the number of order items are : {}", totalPrice, orderItems.size());
            Order order = OrderHelper.processOrder(orderItems, totalPrice, uidx);
            Order savedOrder = orderDAO.saveOrder(order);

            paymentService.pay(savedOrder.getId(), totalPrice);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong inputs were found!");
        } catch (OrderNotPlacedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some error occurred, please retry in some time");
        } catch (PriceChangedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Price has changed, please retry again!");
        } catch (PriceNotFetchedException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Pricing Service Unavailable, please retry again!");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Order created successfully ");
    }

    @Override
    public ResponseEntity updatePaymentStatusForOrder(PaymentStatus paymentStatus, Long orderId) {
        try {
            if (orderId == null || paymentStatus == null) {
                LOGGER.error("Order id or payment status is null");
                throw new IllegalArgumentException("Order id and payment status can't be null");
            }
            Order order = orderDAO.getOrderById(orderId);
            order.setPaymentStatus(paymentStatus);
            orderDAO.saveOrder(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong inputs were found!");
        } catch (OrderNotPlacedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some error occurred, please retry in some time");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No order found for this order id");
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Payment status updated successfully for order id : "+orderId);

    }
}
