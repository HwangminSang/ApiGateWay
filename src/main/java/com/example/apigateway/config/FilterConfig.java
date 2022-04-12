package com.example.apigateway.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

//어노테이션만 주석처리하면 스프링컨테이너가 찾지못헤 빈으로 등록 못함
//설정등록  yml 파일 or pojo 이용
//@Configuration
public class FilterConfig {

    //라우터 등록하기위해 빈등록
    //람다는 익명함수처럼 생각하자
    //  @Bean
    public RouteLocator gatewayRout1es(RouteLocatorBuilder builder) {
        return builder.routes()

                .route(r -> r.path("/first-service/**")  //패스확인후
                        .filters(f -> f.addRequestHeader("first-request", "first-request-header")
                                .addResponseHeader("first-response", "first-response-header"))  //필터적용하고  헤더에 저장시 key value로
                        .uri("http://localhost:8081"))  //uri로 이동

                .route(r -> r.path("/second-service/**")
                        .filters(f -> f.addRequestHeader("second-request", "second-request-header")
                                .addResponseHeader("second-response", "second-request-header"))
                        .uri("http://localhost:8082"))

                .build();

    }
}
