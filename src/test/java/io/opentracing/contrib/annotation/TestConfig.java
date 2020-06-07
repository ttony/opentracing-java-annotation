package io.opentracing.contrib.annotation;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;

@Configuration
@EnableAspectJAutoProxy
@ImportResource("classpath:aop.xml")
class TestConfig {

}
