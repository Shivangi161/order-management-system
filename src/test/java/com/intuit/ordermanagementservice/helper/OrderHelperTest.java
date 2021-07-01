package com.intuit.ordermanagementservice.helper;

import com.intuit.ordermanagementservice.DTOs.ProductDTO;
import com.intuit.ordermanagementservice.DTOs.ProductDTOs;
import com.intuit.ordermanagementservice.enitities.Order;
import com.intuit.ordermanagementservice.enitities.OrderItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.Silent.class)
public class OrderHelperTest {

    ProductDTOs productDTOs = null;
    ProductDTO productDTO = null;
    List<OrderItem> list = new ArrayList<>();
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
        OrderItem orderItem = new OrderItem();
        list.add(orderItem);
    }

    @Test
    public void translateProductsToOrderItems_ValidParamsArePassed_ListOfOrderItem(){
        List<OrderItem> orderItems = OrderHelper.translateProductsToOrderItems(productDTOs.getProductDTOList());
        assertEquals(1,orderItems.size());
        assertEquals(1000d, orderItems.get(0).getDiscountedPrice(),0);

    }

    @Test(expected = IllegalArgumentException.class)
    public void translateProductsToOrderItems_InvalidParamsArePassed_IllegalArgumentException(){
        OrderHelper.translateProductsToOrderItems(null);
    }

    @Test
    public void processOrder_ValidParams_OrderReturned(){
        Order order = OrderHelper.processOrder(list, 100d, "user");
        assertNotNull(order);
        assertEquals(100d,order.getOrderPrice(),0);
    }
}
