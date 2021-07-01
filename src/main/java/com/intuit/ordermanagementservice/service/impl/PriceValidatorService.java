package com.intuit.ordermanagementservice.service.impl;

import com.intuit.ordermanagementservice.DTOs.ProductDTO;
import com.intuit.ordermanagementservice.DTOs.ProductDTOs;
import com.intuit.ordermanagementservice.exception.PriceChangedException;
import com.intuit.ordermanagementservice.exception.PriceNotFetchedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Service
public class PriceValidatorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceValidatorService.class);

    private RestTemplate restTemplate;
    private HttpHeaders headers;

    private static final String URL = "http://localhost:8080/oms/pricing/quote/";
    private static final int RETRY = 2;


    @PostConstruct
    public void init() {
        this.headers = new HttpHeaders();
        this.headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        this.headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    public PriceValidatorService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private boolean validatePrice(ProductDTO productDTO) throws PriceNotFetchedException {
        Double fetchedPrice = fetchPriceFromPricingService(productDTO);
        return fetchedPrice.equals(productDTO.getDiscountedPrice());
    }


    public Double fallback(ProductDTO productDTO, Throwable e) {

        return productDTO.getDiscountedPrice();
    }

    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "default", fallbackMethod = "fallback")
    private Double fetchPriceFromPricingService(ProductDTO productDTO) throws PriceNotFetchedException {
        HttpEntity<?> httpEntity = new HttpEntity<>(this.headers);
        ResponseEntity<Double> response = null;

        int retryCount = 0;
        while (retryCount <= RETRY) {
            try {
                LOGGER.info("get price for the product: {} and validate", productDTO.getProductId());
                response = restTemplate.exchange(URL + productDTO.getProductId(), HttpMethod.GET, httpEntity, Double.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    LOGGER.info("Pricing response: {}", response);
                    break;
                }
                retryCount++;
            } catch (Exception e) {
                LOGGER.error("Error occurred while getting price for #{}:", productDTO.getProductId(), e);
                retryCount++;
            }
        }
        if (response == null) {
            throw new PriceNotFetchedException("Unable to get price from the pricing service for the product id : " + productDTO.getProductId());
        }
        return response.getBody();
    }

    public void validatePriceForProducts(ProductDTOs productDTOs) throws PriceNotFetchedException, PriceChangedException {
        if (productDTOs == null || CollectionUtils.isEmpty(productDTOs.getProductDTOList())) {
            LOGGER.error("Null inputs found in validatePriceForProducts");
            throw new IllegalArgumentException("Not null params required");
        }

        for (ProductDTO productDTO : productDTOs.getProductDTOList()) {
            if (!validatePrice(productDTO)) {
                LOGGER.error("Price has changed for : {}", productDTO);
                throw new PriceChangedException("Price has changed , please retry again!");
            }
        }
    }
}
