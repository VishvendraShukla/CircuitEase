package com.circuitease.executorservice;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("circuitBreakerExecutorService")
public class CircuitBreakerExecutorService {

  @Autowired
  private CircuitBreakerRegistry circuitBreakerRegistry;

  public <T> T executeWithFallback(String circuitBreakerName, Supplier<T> actualMethod,
      Supplier<T> fallbackMethod) throws IllegalArgumentException {

    CircuitBreaker circuitBreaker = retrieveCircuitBreakerByName(circuitBreakerName);
    Supplier<T> decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, actualMethod);

    try {
      return decoratedSupplier.get();
    } catch (Exception e) {
      log(circuitBreakerName, e);
      return fallbackMethod.get();
    }
  }

  public <T> void executeWithoutFallbackAndLog(String circuitBreakerName,
      Supplier<T> actualMethod) throws IllegalArgumentException {
    CircuitBreaker circuitBreaker = retrieveCircuitBreakerByName(circuitBreakerName);
    Supplier<T> decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, actualMethod);
    try {
      decoratedSupplier.get();
    } catch (Exception e) {
      log(circuitBreakerName, e);
    }
  }

  public void executeWithoutFallbackAndLog(String circuitBreakerName, Runnable actualMethod)
      throws IllegalArgumentException {
    CircuitBreaker circuitBreaker = retrieveCircuitBreakerByName(circuitBreakerName);
    Runnable decoratedRunnable = CircuitBreaker.decorateRunnable(circuitBreaker, actualMethod);
    try {
      decoratedRunnable.run();
    } catch (Exception e) {
      log(circuitBreakerName, e);
    }
  }

  private CircuitBreaker retrieveCircuitBreakerByName(String circuitBreakerName)
      throws IllegalArgumentException {
    return circuitBreakerRegistry
        .getAllCircuitBreakers()
        .stream()
        .filter(circuitBreaker -> circuitBreaker.getName().equalsIgnoreCase(circuitBreakerName))
        .findFirst().orElseThrow(() -> new IllegalArgumentException(
            "Circuit breaker with name " + circuitBreakerName + " not found."));
  }

  private void log(String circuitBreakerName, Exception e) {
    log.error("Error in circuit breaker: {} , exception: {}", circuitBreakerName, e.getMessage());
  }
}
