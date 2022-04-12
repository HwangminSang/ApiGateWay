package com.example.apigateway.filter;


import io.jsonwebtoken.Jwts;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;



@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    private  Environment env;

    public  AuthorizationHeaderFilter(Environment env){
          super(Config.class);
          this.env=env;
      }


       public  static class Config{

    }

    // login -> token  -> users(with token) -> header(include token)
    @Override
    public GatewayFilter apply(Config config) {
        //토큰을 꺼낼준비
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            //헤더에 포함된 토큰정보 확인

            //1 토큰 유무 확인 없을경우 에러를 던짐
            if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                return onError(exchange,"no authorization header", HttpStatus.UNAUTHORIZED);
            }

            //2.토큰이 있을경우 헤더에서 꺼내서 가져온다. list형태
            String authorizationHeader =request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            //3.Bearer이름은 없애주고 ( 문자열 ) 순수함 토큰값을 받는다
            String jwt=authorizationHeader.replace("Bearer","");

            //4. 토큰이 유효한지 유효성 검사를 한다
            if(!isJwtValid(jwt)){
                //유효하지 않을경우
                return onError(exchange,"JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);

        });
    }

    //토큰 유효성
    private boolean isJwtValid(String jwt) {
           boolean returnValue = true;

           String subject=null;
           // jwt.io에서 확인 해보면 토큰에서 subject를 먼저 추출해서 정상인지 확인
        //토큰을 만들때 암호화를 시켰으니 복호화 하는 과정에서 들어갔던 이름을 사용
        //복호화후 parseClaimsJws() 파싱해서 body의 subject만 꺼낸다
        try{
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(jwt).getBody()
                    .getSubject();

        }catch (Exception ex){
            returnValue=false;
        }
        // null or 비워있을경우
        if(subject==null || subject.isEmpty()){
            returnValue=false;
        }
        return  returnValue;
       }

    // 에러 발생시 해당값을 response
    // web flux에서 데이터를 처리하는 단위값 중의 하나가 Mono
    //Mono  , Flux --> Spring WebFlux(데이터를 비동기처리)   <단일값  Mono > <다중값 Flux> 반환
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse(); //클라이언트에게 반환
        response.setStatusCode(httpStatus);//상태코드입력

        log.error(err);
        //mono타입반환시 setComplete(); 사용
        return response.setComplete();

    }

}

