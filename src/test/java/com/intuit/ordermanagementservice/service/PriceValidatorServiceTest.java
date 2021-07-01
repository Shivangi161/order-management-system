package com.intuit.ordermanagementservice.service;

import com.intuit.ordermanagementservice.DTOs.ProductDTO;
import com.intuit.ordermanagementservice.DTOs.ProductDTOs;
import com.intuit.ordermanagementservice.enitities.Order;
import com.intuit.ordermanagementservice.exception.PriceChangedException;
import com.intuit.ordermanagementservice.exception.PriceNotFetchedException;
import com.intuit.ordermanagementservice.service.impl.PriceValidatorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PriceValidatorServiceTest {


    @InjectMocks
    private PriceValidatorService priceValidatorService;

    @Mock
    private RestTemplate restTemplate;

    HttpHeaders httpHeaders = null;
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
        productDTO.setDiscountedPrice(200d);
        productDTO.setMrp(1111d);
        productDTO.setName("shirt");
        productDTO.setProductId(1L);
        List<ProductDTO> productDTOList = new ArrayList<>();
        productDTOList.add(productDTO);
        productDTOs.setProductDTOList(productDTOList);
        order = new Order();
        order.setId(1L);

        httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    public void validatePriceForProducts_PassedValidParameters_ValidatedSuccessfully() throws PriceNotFetchedException, PriceChangedException {
        ResponseEntity<Double> response = new ResponseEntity<>(200d, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Double.class))).thenReturn(response);
        priceValidatorService.validatePriceForProducts(productDTOs);
    }

    @Test(expected = PriceChangedException.class)
    public void validatePriceForProducts_PriceChange_PriceChangeException() throws PriceNotFetchedException, PriceChangedException {
        ResponseEntity<Double> response = new ResponseEntity<>(100d, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Double.class))).thenReturn(response);
        priceValidatorService.validatePriceForProducts(productDTOs);
    }


    @Test(expected = IllegalArgumentException.class)
    public void validatePriceForProducts_NullParamsPassed_IllegalArgumentException() throws PriceNotFetchedException, PriceChangedException {
        priceValidatorService.validatePriceForProducts(null);
    }

    @Test(expected = PriceChangedException.class)
    public void validatePriceForProducts_PricingServiceUnavailable_PriceNotFetchedException() throws PriceNotFetchedException, PriceChangedException {
        ResponseEntity<Double> response = new ResponseEntity<>(0d, HttpStatus.SERVICE_UNAVAILABLE);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Double.class))).thenReturn(response);
        priceValidatorService.validatePriceForProducts(productDTOs);
    }

    @Test(expected = PriceNotFetchedException.class)
    public void validatePriceForProducts_PricingServiceThrowingException_PriceNotFetchedException() throws PriceNotFetchedException, PriceChangedException {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Double.class))).thenThrow(RuntimeException.class);
        priceValidatorService.validatePriceForProducts(productDTOs);
    }
}
