[![Build Status](https://travis-ci.com/ttony/opentracing-java-annotation.svg?branch=master)](https://travis-ci.com/ttony/opentracing-java-annotation)

# OpenTracing Java Annotation

This library is a java annotation driven for OpenTracing.

## Usage

### Creating A New Span

```java
@NewSpan
public void calculateTax(TaxModel model) {
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
