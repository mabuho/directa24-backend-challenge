package com.directa24.challenge.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties("directa24.retry")
public class RetryConfig {

    private int maxAttempt;
    private int timeInterval;
    private List<Class<? extends Throwable>> throwable;

    @Bean
    public RetryTemplate retryTemplate() {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts( maxAttempt );

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod( timeInterval );

        return RetryTemplate.builder()
                .customPolicy( retryPolicy )
                .customBackoff( backOffPolicy )
                .retryOn(throwable)
                .build();
    }

}

