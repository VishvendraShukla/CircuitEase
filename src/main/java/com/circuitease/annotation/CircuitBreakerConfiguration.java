package com.circuitease.annotation;

import com.circuitease.model.ICircuitBreakerProperties;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface CircuitBreakerConfiguration {

    String circuitBreakerName();

    Class<? extends ICircuitBreakerProperties> circuitBreakerConfigurationPropertiesClass();
}
