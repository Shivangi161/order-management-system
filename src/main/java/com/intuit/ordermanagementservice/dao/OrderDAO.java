package com.intuit.ordermanagementservice.dao;

import com.intuit.ordermanagementservice.enitities.Order;
import com.intuit.ordermanagementservice.exception.OrderNotPlacedException;
import com.intuit.ordermanagementservice.repository.OrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Component
public class OrderDAO {

    private OrderRepository orderRepository;

    public OrderDAO(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public Order saveOrder(Order order) throws OrderNotPlacedException {
        if (order == null || CollectionUtils.isEmpty(order.getOrderItems())) {
            throw new IllegalArgumentException("Order and order items are expected to be non null and non empty");
        }
        try {
            order.getOrderItems().forEach(orderItem -> orderItem.setOrders(order));
            return orderRepository.save(order);
        } catch (Exception e) {
            throw new OrderNotPlacedException("Exception occurred! Please retry");
        }
    }


    public Order getOrderById(Long orderId)  {
        if (orderId == null) {
            throw new IllegalArgumentException("Order id must not be null");
        }
        return orderRepository.getOne(orderId);

    }


}
