package com.intuit.ordermanagementservice.configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MyConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CircuitBreakerConfigCustomizer testCustomizer() {
        return CircuitBreakerConfigCustomizer
                .of("default", builder -> builder.slidingWindowSize(10).failureRateThreshold(70.0f).slidingWindowType(
                        CircuitBreakerConfig.SlidingWindowType.COUNT_BASED));
    }
}
