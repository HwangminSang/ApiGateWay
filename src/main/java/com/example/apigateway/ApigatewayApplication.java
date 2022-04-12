package com.example.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication

public class ApigatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApigatewayApplication.class, args);
    }


    /**
     * 클라이언트의 요청의 trace를 메모리에 담겨  HttpTrace 요청시 사용가능
     */
    @Bean
    public HttpTraceRepository httpTraceRepository(){
            return new InMemoryHttpTraceRepository();
    }

}
