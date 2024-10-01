package com.decrypto.operacionescrud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EntityScan(basePackages = "com.decrypto.operacionescrud.entities")
@EnableCaching
public class OperacionesCrudApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(OperacionesCrudApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(OperacionesCrudApplication.class, args);
    }

}
