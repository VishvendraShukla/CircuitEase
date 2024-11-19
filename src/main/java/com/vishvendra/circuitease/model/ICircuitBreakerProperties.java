package com.vishvendra.circuitease.model;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

public abstract class ICircuitBreakerProperties {

    public abstract CircuitBreakerConfig circuitBreakerConfig();

    public abstract TimeLimiterConfig timeLimiterConfig();

}
