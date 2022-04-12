package com.example.apigateway.filter;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() {
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


        //GatewayFilter<인터페이스> 구현 클래스로 객체생성 or 람다형식으로 익명클래스로 객체생성
        //exchange이용  ServerHttpRequest or ServerHttpResponse 구하고 / 필터 chain 할떄 쓴다.
        //OrderedGatewayFilter 필터 적용 순서를 적용해줄수있다.
        GatewayFilter filter = new OrderedGatewayFilter((exchange, chain) -> {
            //구현내용
            //Custom Pre Filter

            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("LoggingFilter baseMessage: {}", config.getBaseMessage());

            //유무확인
            if (config.isPreLogger()) {
                log.info("LoggingFilter Pre: request id ->{}", request.getId());
            }

            //Custom Post Filter   비동기방식 단일값 전달시 Mono이용
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {


                //포스트 로그가 작동되어야 한다면
                if (config.isPostLogger()) {
                    log.info("LoggingFilter post: request code ->{}", response.getStatusCode());
                }
            }));

        }, Ordered.LOWEST_PRECEDENCE);  //필터 순서 :   Ordered.HIGHEST_PRECEDENCE 가장먼저
        //             Ordered.LOWEST_PRECEDENCE 가장마지막

        return filter;
    }

    ;


}

