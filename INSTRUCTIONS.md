# CircuitEase Usage Instructions
CircuitEase simplifies integrating Resilience4J's Circuit Breaker with Spring projects through a custom `@CircuitBreakerConfiguration` annotation. This allows developers to manage Circuit Breaker configurations directly using a declarative approach.

## Table of Contents
* **[Getting Started](#getting-started)**
* **[Creating a Circuit Breaker Configuration](#creating-a-circuit-breaker-configuration)**
* **[Using the @CircuitBreakerConfiguration Annotation](#using-the-circuitbreakerconfiguration-annotation)**
* **[Customizing Circuit Breaker Settings](#customizing-circuit-breaker-settings)**
* **[Example](#Example)**
* **[How to Use CircuitBreakerExecutorService in Your Spring Project](#how-to-use-circuitbreakerexecutorservice-in-your-spring-project)**



### Getting Started
To get started, add CircuitEase to your project as a Maven dependency

```xml 
<dependency>
    <groupId>com.vishvendra</groupId>
    <artifactId>curcuitease</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Creating a Circuit Breaker Configuration
To use the custom `@CircuitBreakerConfiguration` annotation, follow these steps:

* Create a class that will serve as the configuration for your Circuit Breaker.
* Annotate the class with `@CircuitBreakerConfiguration`.
* Specify the `circuitBreakerName` and the class that defines your Circuit Breaker properties.

#### Example: Basic Setup

```java
import com.vishvendra.circuitease.annotations.CircuitBreakerConfiguration;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@CircuitBreakerConfiguration(
    circuitBreakerName = "backend-service-a",
    circuitBreakerConfigurationPropertiesClass = BackendServiceACircuitBreakerProps.class
)
public class BackendServiceACircuitBreakerConfig {

  public static class BackendServiceACircuitBreakerProps extends ICircuitBreakerProperties {

    @Override
    public CircuitBreakerConfig circuitBreakerConfig() {
      // Define the default configuration for the Circuit Breaker
      return CircuitBreakerConfig.ofDefaults();
    }

    @Override
    public TimeLimiterConfig timeLimiterConfig() {
      // Define the default configuration for the Time Limiter
      return TimeLimiterConfig.ofDefaults();
    }
  }
}
```
### Using the `@CircuitBreakerConfiguration` Annotation
#### Parameters
* `circuitBreakerName` - The unique name for the Circuit Breaker instance. This helps identify the specific circuit breaker settings in your application.
* `circuitBreakerConfigurationPropertiesClass` - A class that extends ICircuitBreakerProperties to provide custom configurations for the Circuit Breaker and Time Limiter.

#### Steps
1. **Create the Configuration Class:** Create a new class in your project, and annotate it with `@CircuitBreakerConfiguration`.
2. **Specify Circuit Breaker Properties:** Implement a nested static class that extends `ICircuitBreakerProperties` and override the required methods:
   + `circuitBreakerConfig()`: Define the configuration for the Circuit Breaker.
   + `timeLimiterConfig()`: Define the configuration for the Time Limiter.

### Customizing Circuit Breaker Settings
By default, the configuration methods (`circuitBreakerConfig` and `timeLimiterConfig`) return the default settings provided by Resilience4J. You can customize these settings to suit your specific needs.

#### Example: Custom Circuit Breaker Settings

```java
@Override
public CircuitBreakerConfig circuitBreakerConfig() {
  return CircuitBreakerConfig.custom()
      .failureRateThreshold(50) // Opens the circuit if 50% of requests fail
      .waitDurationInOpenState(Duration.ofSeconds(10)) // Duration to stay open before retrying
      .slidingWindowSize(20) // Number of calls to consider for statistics
      .build();
}

@Override
public TimeLimiterConfig timeLimiterConfig() {
  return TimeLimiterConfig.custom()
      .timeoutDuration(Duration.ofSeconds(5)) // Timeout after 5 seconds
      .build();
}
```

### Example
Below is a complete example showing how to configure and use the `@CircuitBreakerConfiguration` annotation

```java
import com.vishvendra.circuitease.annotations.CircuitBreakerConfiguration;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import java.time.Duration;

@CircuitBreakerConfiguration(
    circuitBreakerName = "backend-service-b",
    circuitBreakerConfigurationPropertiesClass = BackendServiceBCircuitBreakerProps.class
)
public class BackendServiceBCircuitBreakerConfig {

  public static class BackendServiceBCircuitBreakerProps extends ICircuitBreakerProperties {

    @Override
    public CircuitBreakerConfig circuitBreakerConfig() {
      // Custom Circuit Breaker configuration
      return CircuitBreakerConfig.custom()
          .failureRateThreshold(60) // 60% failure rate threshold
          .waitDurationInOpenState(Duration.ofSeconds(15)) // Stay open for 15 seconds
          .build();
    }

    @Override
    public TimeLimiterConfig timeLimiterConfig() {
      // Custom Time Limiter configuration
      return TimeLimiterConfig.custom()
          .timeoutDuration(Duration.ofSeconds(3)) // Timeout after 3 seconds
          .build();
    }
  }

}
```

In this example, the `BackendServiceBCircuitBreakerConfig` class defines a circuit breaker configuration with a custom failure rate threshold and a timeout duration.

### How to Use CircuitBreakerExecutorService in Your Spring Project
The CircuitBreakerExecutorService is designed to be used across different services in your Spring Boot application. It abstracts the complexity of manually managing circuit breakers and fallback logic. Here's how you can integrate it into your Spring services:

#### 1. Inject `CircuitBreakerExecutorService` into Your Service
   You can inject `CircuitBreakerExecutorService` into any Spring service that needs to use a circuit breaker. This service allows you to define the actual method and fallback method to be executed based on the state of the circuit breaker.
   
```java
import com.vishvendra.circuitease.executorservice.CircuitBreakerExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SomeService {

  @Autowired
  private CircuitBreakerExecutorService circuitBreakerExecutorService;

  public String someMethod() {
    return circuitBreakerExecutorService.executeWithFallback("backend-service-a", 
        () -> "actual response",  // Actual method to run
        () -> "fallback response");  // Fallback method in case of failure
  }
}
```

#### 2. How the Service Works
   + `executeWithFallback`: This method takes in the name of the circuit breaker ("backend-service-a") and two Supplier functions:
     + `The actual method`: A method that will be executed if the circuit breaker is closed and the service is healthy. 
     + `The fallback method`: A method that will be executed if the circuit breaker is open, or if the actual method fails (e.g., due to timeouts or service failures).
   + `CircuitBreakerExecutorService` abstracts the logic of interacting with Resilience4J's `CircuitBreaker`, so you can focus on defining the behavior for both success and failure cases.