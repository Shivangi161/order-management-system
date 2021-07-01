package com.intuit.ordermanagementservice.service;

import com.intuit.ordermanagementservice.DTOs.ProductDTO;
import com.intuit.ordermanagementservice.DTOs.ProductDTOs;
import com.intuit.ordermanagementservice.clients.PaymentService;
import com.intuit.ordermanagementservice.dao.OrderDAO;
import com.intuit.ordermanagementservice.enitities.Order;
import com.intuit.ordermanagementservice.enums.PaymentStatus;
import com.intuit.ordermanagementservice.exception.OrderNotPlacedException;
import com.intuit.ordermanagementservice.exception.PriceChangedException;
import com.intuit.ordermanagementservice.exception.PriceNotFetchedException;
import com.intuit.ordermanagementservice.service.impl.OrderService;
import com.intuit.ordermanagementservice.service.impl.PriceValidatorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class OrderServiceTest {

    @InjectMocks
    OrderService orderService;

    @Mock
    private PaymentService paymentService;
    @Mock
    private OrderDAO orderDAO;
    @Mock
    private PriceValidatorService priceValidatorService;

    ProductDTOs productDTOs = null;
    Order order = null;
    ProductDTO productDTO = null;

    @Before
    public void beforeEachTestCase() {
        initialize();
    }

    public void initialize() {
        productDTOs = new ProductDTOs();
        productDTO = new ProductDTO();
        productDTO.setDiscountedPrice(1000d);
        productDTO.setMrp(1111d);
        productDTO.setName("shirt");
        productDTO.setProductId(1L);
        List<ProductDTO> productDTOList = new ArrayList<>();
        productDTOList.add(productDTO);
        productDTOs.setProductDTOList(productDTOList);
        order = new Order();
        order.setId(1L);
    }

    @Test
    public void createOrder_PassedValidParameters_OrderCreatedSuccessfully() throws PriceNotFetchedException, OrderNotPlacedException, PriceChangedException {
        when(orderDAO.saveOrder(any())).thenReturn(order);
        ResponseEntity res = orderService.createOrder(productDTOs, "abc");
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test
    public void createOrder_PriceChangeHappens_PriceChangeException() throws PriceNotFetchedException, OrderNotPlacedException, PriceChangedException {
        doThrow(PriceChangedException.class).when(priceValidatorService).validatePriceForProducts(any());
        when(orderDAO.saveOrder(any())).thenReturn(order);
        ResponseEntity res = orderService.createOrder(productDTOs, "abc");
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void createOrder_SendingNullInput_IllegalArgumentException() throws PriceNotFetchedException, OrderNotPlacedException {
        ResponseEntity res = orderService.createOrder(null, "abc");
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }


    @Test
    public void createOrder_ProductDtoPassedNull_IllegalArgumentException() throws PriceNotFetchedException, OrderNotPlacedException {
        ResponseEntity res = orderService.createOrder(null, "abc");
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void createOrder_saveOrderFailing_InternalServerError() throws PriceNotFetchedException, OrderNotPlacedException {
        when(orderDAO.saveOrder(any())).thenThrow(OrderNotPlacedException.class);
        ResponseEntity res = orderService.createOrder(productDTOs, "abc");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatusCode());
    }

    @Test
    public void createOrder_PricingServiceFailing_ServiceUnavailable() throws PriceNotFetchedException, OrderNotPlacedException, PriceChangedException {
        doThrow(PriceNotFetchedException.class).when(priceValidatorService).validatePriceForProducts(any());
        ResponseEntity res = orderService.createOrder(productDTOs, "abc");
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, res.getStatusCode());
    }


    @Test
    public void updatePaymentStatusForOrder_ValidParamsPassed_PaymentStatusUpdated() {
        when(orderDAO.getOrderById(any())).thenReturn(order);
        ResponseEntity responseEntity = orderService.updatePaymentStatusForOrder(PaymentStatus.PAID, 1L);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    }

    @Test
    public void updatePaymentStatusForOrder_PaymentStatusPassedAsNull_BadRequest() {
        ResponseEntity responseEntity = orderService.updatePaymentStatusForOrder(null, 1L);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void updatePaymentStatusForOrder_SaveOrderFailing_InternalServerError() throws OrderNotPlacedException {
        when(orderDAO.getOrderById(any())).thenReturn(order);
        when(orderDAO.saveOrder(any())).thenThrow(OrderNotPlacedException.class);
        ResponseEntity responseEntity = orderService.updatePaymentStatusForOrder(PaymentStatus.PAID, 1L);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void updatePaymentStatusForOrder_NoOrderFoundForOrderId_BadRequest() throws OrderNotPlacedException {
        when(orderDAO.getOrderById(any())).thenThrow(EntityNotFoundException.class);
        ResponseEntity responseEntity = orderService.updatePaymentStatusForOrder(PaymentStatus.PAID, 1L);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void updatePaymentStatusForOrder_paymentFailedForPrepaidOrder_orderMarkedasCOD() {
        when(orderDAO.getOrderById(any())).thenReturn(order);
        ResponseEntity responseEntity = orderService.updatePaymentStatusForOrder(PaymentStatus.PAYMENT_FAILED, 1L);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
    }
}
