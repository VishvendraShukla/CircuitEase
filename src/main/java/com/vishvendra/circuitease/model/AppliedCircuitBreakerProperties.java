package com.vishvendra.circuitease.model;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppliedCircuitBreakerProperties {

  private String breakerName;
  private CircuitBreakerConfig circuitBreakerConfig;
  private TimeLimiterConfig timeLimiterConfig;
}
