package entity;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @ClassName FeignInterceptor
 * @Description 服务间调用需要的头信息
 * @Author 传智播客
 * @Date 14:50 2020/9/17
 * @Version 2.1
 **/
public class FeignInterceptor implements RequestInterceptor {

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
    }
}
