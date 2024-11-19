package com.vishvendra.circuitease.config;

import com.vishvendra.circuitease.annotation.CircuitBreakerConfiguration;
import com.vishvendra.circuitease.model.AppliedCircuitBreakerProperties;
import com.vishvendra.circuitease.model.ICircuitBreakerProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import jakarta.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class BaseCircuitBreakerConfiguration {

  List<AppliedCircuitBreakerProperties> appliedCircuitBreakerProperties;
  @Autowired
  private ApplicationContext applicationContext;

  @PostConstruct
  public void loadCircuitBreakerConfigurations() {
    Map<String, Object> annotatedBeans = applicationContext.getBeansWithAnnotation(
        CircuitBreakerConfiguration.class);
    appliedCircuitBreakerProperties = new ArrayList<>();
    if (annotatedBeans.isEmpty()) {
      log.info("No circuit breaker configurations registered with CircuitEase.");
      return;
    }
    annotatedBeans.forEach((beanName, bean) -> {
      CircuitBreakerConfiguration annotation = bean.getClass()
          .getAnnotation(CircuitBreakerConfiguration.class);

      if (annotation != null) {
        try {
          String circuitBreakerName = annotation.circuitBreakerName();
          Class<? extends ICircuitBreakerProperties> configClass = annotation.circuitBreakerConfigurationPropertiesClass();
          ICircuitBreakerProperties properties = configClass.getDeclaredConstructor().newInstance();
          appliedCircuitBreakerProperties.add(
              new AppliedCircuitBreakerProperties(circuitBreakerName,
                  properties.circuitBreakerConfig(), properties.timeLimiterConfig()));
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
          log.error("Error while loading circuit breaker configurations", e);
        }

      } else {
        log.info("No annotation found for bean: {}", beanName);
      }
    });
  }

  @Bean
  public CircuitBreakerRegistry circuitBreakerRegistry() {
    handleEmptyList();
    CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
    appliedCircuitBreakerProperties.forEach(
        circuitBreaker -> registry.circuitBreaker(circuitBreaker.getBreakerName(),
            circuitBreaker.getCircuitBreakerConfig()));
    return registry;
  }

  @Bean
  public TimeLimiterRegistry timeLimiterRegistry() {
    handleEmptyList();
    TimeLimiterRegistry timeLimiterRegistry = TimeLimiterRegistry.ofDefaults();
    appliedCircuitBreakerProperties.forEach(
        circuitBreaker -> timeLimiterRegistry.timeLimiter(circuitBreaker.getBreakerName(),
            circuitBreaker.getTimeLimiterConfig()));
    return timeLimiterRegistry;
  }

  private void handleEmptyList() {
    if (Objects.isNull(appliedCircuitBreakerProperties)) {
      loadCircuitBreakerConfigurations();
    }
  }
}
