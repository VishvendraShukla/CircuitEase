# CircuitEase

CircuitEase is a lightweight library that simplifies the usage of the [Resilience4J](https://resilience4j.readme.io/) Circuit Breaker through custom Spring annotations. This project provides a seamless way to integrate circuit-breaking capabilities into your Spring-based applications with minimal effort, using newly created annotations to manage Resilience4J settings.

## Features

- **Custom Spring Annotation** for integrating Resilience4J Circuit Breaker functionality.
- Simple to configure and use with Spring projects.
- Easily handle fault tolerance and resiliency in your services.
- Fine-tune Resilience4J configurations through annotations.

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.vishvendra</groupId>
    <artifactId>curcuitease</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Clone the Project
If you prefer to clone the project directly instead of using Maven, you can do so with the following command:

```bash
# Clone using HTTPS
git clone https://github.com/VishvendraShukla/CircuitEase.git

OR

# Clone using SSH
git clone git@github.com:VishvendraShukla/CircuitEase.git

cd /path/to/circuitease

mvn clean install
```