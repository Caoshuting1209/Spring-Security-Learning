package com.shuting.springsecurityoauth2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.shuting.springsecurityoauth2.mapper")
public class SpringSecurityOauth2Application {

  public static void main(String[] args) {
    SpringApplication.run(SpringSecurityOauth2Application.class, args);
  }
}
