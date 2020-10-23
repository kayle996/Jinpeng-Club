package com.changgou.filter;

import com.changgou.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @ClassName AuthorizeFilter
 * @Description
 * @Author 传智播客
 * @Date 12:03 2020/9/14
 * @Version 2.1
 **/
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered{

    // 定义常量
    private static final String AUTHORIZE_TOKEN = "Authorization";

    private static final String LOGIN_URL = "http://localhost:9001/oauth/login";    // 登录页面


    /**
     * @author 栗子
     * @Description 判断用户是否登录
     * @Date 12:04 2020/9/14
     * @param exchange
     * @param chain
     * @return reactor.core.publisher.Mono<java.lang.Void>
     **/
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取request和response
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 判断请求是否是登录操作
        String uri = request.getURI().getPath();
        if (uri.startsWith("/api/user/login")){
            // 登录操作，直接放行
            return chain.filter(exchange);
        }

        // 访问其他资源，需要判断是否登录  //url?Authorization=xxx
        // 从请求参数获取token
        String token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        if (StringUtils.isEmpty(token)){
            // 再从请求头获取
            token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        }
        if (StringUtils.isEmpty(token)){
            // 从cookie中获取
            HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if (cookie != null){
                token = cookie.getValue();
            }
        }

        // 从三个地方取token
        if (StringUtils.isEmpty(token)){
            System.out.println("兄弟，你还没有登录呢。。。");
            // 没有取到，不放行
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);    // 401，无效认证
            response.setStatusCode(HttpStatus.SEE_OTHER);
            // 用户没有登录，需要踢回到登录页面（需要带上当前的地址）

            String url = request.getURI().toString();
            String location = LOGIN_URL + "?ReturnUrl=" + url;
            response.getHeaders().add("Location", location);
            return response.setComplete();
        }

        // 不为空，解析token
        try {
            // 解析成功，放行 day08
//            JwtUtil.parseJWT(token);
            // day10  网关获取token后，放入请求头中
            request.mutate().header("Authorization", "Bearer " + token);
        } catch (Exception e) {
            e.printStackTrace();
            // 解析失败，不放行
            response.setStatusCode(HttpStatus.UNAUTHORIZED);    // 401，无效认证
            return response.setComplete();
        }
        return chain.filter(exchange);
    }

    /**
     * @author 栗子
     * @Description 指定过滤器的执行顺序（优先级）
     * @Date 12:03 2020/9/14
     * @param
     * @return int
     **/
    @Override
    public int getOrder() {
        return 0;
    }
}
