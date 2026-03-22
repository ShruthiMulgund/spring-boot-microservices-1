package com.practice.discovery_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerApplication {
    public static void main(String[] args) {

        SpringApplication.run(DiscoveryServerApplication.class, args);
        int[] arr = {1, 0, 1, 0, 1, 0, 1, 0};
        Arrays.stream(arr)
                .boxed()
                .sorted((a,b) -> b-a)
                .mapToInt(Integer::intValue)
                .toArray().toString();
    }
}
