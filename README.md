[![Build Status](https://travis-ci.com/ttony/opentracing-java-annotation.svg?branch=master)](https://travis-ci.com/ttony/opentracing-java-annotation) [![Maven Central](https://img.shields.io/maven-central/v/io.github.ttony/opentracing-java-annotation.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.ttony%22%20AND%20a:%22opentracing-java-annotation%22) 

# OpenTracing Java Annotation

This library is a java annotation driven for OpenTracing.

> Note: The annotation concept is purely comes from Spring Sleuth project.

## Dependency
```xml
<dependency>
    <groupId>io.github.ttony</groupId>
    <artifactId>opentracing-java-annotation</artifactId>
    <version>1.0.1</version>
</dependency>
```
## Usage

### Creating A New Span

```java
@NewSpan
public void calculateTax(TaxModel model) {
    ...
}
```

### Tag A New Span
```java
@NewSpan
public void calculateTax(TaxModel model, @SpanTag("tag-name") String tagValue) {
    ...
}
```

### Injecting A Span


```java
@NewSpan
public void calculateTax(Span span, TaxModel model) {
    span.log("event logging");
    ...
}
```

### Override Default Span's OperationName


```java
@NewSpan(operationName="calculateGST")
public void calculateTax(Span span, TaxModel model) {
    span.log("event logging");
    ...
}
```

## Integrating SpringBoot

Register the Aspect class as spring bean:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

    <bean class="io.opentracing.contrib.annotation.handler.NewSpanHandler" />
</beans>
```

Include SpringBoot AOP dependencies:
```groovy
compile 'org.springframework.boot:spring-boot-starter-aop'
```

