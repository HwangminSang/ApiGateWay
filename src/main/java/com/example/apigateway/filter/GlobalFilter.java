package com.example.apigateway.filter;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {
        super(Config.class);
    }


    @Data
    public static class Config {
        //CustomFilter.Config 때문에 inner클래스만들기
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }

    //구현할것
    @Override
    public GatewayFilter apply(Config config) {
        //Custom Pre Filter
        return (exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("GlobalFilter baseMessage: {}", config.getBaseMessage());

            //유무확인
            if (config.isPreLogger()) {
                log.info("GlobalFilter start: request id ->{}", request.getId());
            }

            //Custom Post Filter   비동기방식 단일값 전달시 Mono이용
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("GlobalFilter Post Filter: {}", config.getBaseMessage());

                //포스트 로그가 작동되어야 한다면
                if (config.isPostLogger()) {
                    log.info("GlobalFilter end: request code ->{}", response.getStatusCode());
                }
            }));
        };


    }
}
