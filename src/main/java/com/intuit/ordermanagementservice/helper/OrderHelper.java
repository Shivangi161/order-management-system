package com.intuit.ordermanagementservice.helper;

import com.intuit.ordermanagementservice.DTOs.ProductDTO;
import com.intuit.ordermanagementservice.enitities.Order;
import com.intuit.ordermanagementservice.enitities.OrderItem;
import com.intuit.ordermanagementservice.enums.OrderStatus;
import com.intuit.ordermanagementservice.enums.PaymentStatus;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderHelper {

    public static List<OrderItem> translateProductsToOrderItems(List<ProductDTO> productDTOS){
        if(CollectionUtils.isEmpty(productDTOS)){
            throw new IllegalArgumentException("List of product dtos of atleast size=1 is expected");
        }
        List<OrderItem> orderItems = new ArrayList<>();
        // orderItems.setExpectedDeliveryDate(); TODO Shivangi get this from delivery service
        productDTOS.forEach(productDTO -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setMrp(productDTO.getMrp());
            orderItem.setDiscountedPrice(productDTO.getDiscountedPrice());
            orderItem.setProductId(productDTO.getProductId());
            orderItem.setName(productDTO.getName());
            orderItems.add(orderItem);
        });

        return orderItems;

    }


    public static Order processOrder(List<OrderItem> orderItems, Double totalPrice, String uidx){
        Order order = new Order();
        order.setCreatedAt(new Date());
        order.setOrderItems(orderItems);
        order.setUidx(uidx);
        order.setOrderStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.WAITING_FOR_PAYMENT);
        order.setOrderPrice(totalPrice);
        return order;
    }

}
