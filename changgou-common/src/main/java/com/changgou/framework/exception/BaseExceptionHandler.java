package com.changgou.framework.exception;

import entity.Result;
import entity.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName BaseExceptionHandler
 * @Description 统一异常的处理
 * @Author 传智播客
 * @Date 16:07 2020/9/3
 * @Version 2.1
 **/
@ControllerAdvice   // 发送的请求：Controller  对controller中的方法进行增强
public class BaseExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        // 上线后
//        return new Result(false, StatusCode.ERROR, "服务器睡着了");
        return new Result(false, StatusCode.ERROR, e.getMessage());
    }

}
