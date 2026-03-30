package com.property;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.property.**.mapper")
@EnableAsync
public class PropertyApplication {
    public static void main(String[] args) {
        SpringApplication.run(PropertyApplication.class, args);
    }
}
