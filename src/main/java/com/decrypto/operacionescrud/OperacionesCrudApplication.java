package com.decrypto.operacionescrud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@EntityScan(basePackages = "com.decrypto.operacionescrud.entities")
public class OperacionesCrudApplication {

    public static void main(String[] args) {
        SpringApplication.run(OperacionesCrudApplication.class, args);
    }

}
