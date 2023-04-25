package org.fffd.l23o6;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class L23o6Application {

    public static void main(String[] args) {
        SpringApplication.run(L23o6Application.class, args);
    }

}
