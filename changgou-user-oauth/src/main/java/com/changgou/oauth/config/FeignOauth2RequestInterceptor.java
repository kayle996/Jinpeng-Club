package com.changgou.oauth.config;

import com.changgou.oauth.util.JwtToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @ClassName FeignOauth2RequestInterceptor
 * @Description feign调用的过程中设置请求头
 * @Author 传智播客
 * @Date 12:03 2020/9/17
 * @Version 2.1
 **/
@Component
public class FeignOauth2RequestInterceptor implements RequestInterceptor {


    @Override
    public void apply(RequestTemplate template) {
        // 获取所有的请求头信息（request）
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null){
            // 本次请求的request
            HttpServletRequest request = attributes.getRequest();
            // 获取所有的头信息
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()){
                String headName = headerNames.nextElement();
                String headerValue = request.getHeader(headName);

                // 放入下次请求头中
                template.header(headName, headerValue);
            }

        }

        // 该token中：必须包含权限信息
        String token = JwtToken.adminJwt();
        // 将token放入请求头（通过feign携带）
        template.header("Authorization", "bearer " + token);
    }
}
