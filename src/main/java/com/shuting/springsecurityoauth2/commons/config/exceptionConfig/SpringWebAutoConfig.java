package com.shuting.springsecurityoauth2.commons.config.exceptionConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringWebAutoConfig implements WebMvcConfigurer {
  @Bean
  public FlowExceptionHandler flowExceptionHandler() {
    return new FlowExceptionHandler();
  }
}
