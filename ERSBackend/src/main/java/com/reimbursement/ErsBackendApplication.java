package com.reimbursement;

import com.reimbursement.config.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CorsProperties.class)
public class ErsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErsBackendApplication.class, args);
    }
}
