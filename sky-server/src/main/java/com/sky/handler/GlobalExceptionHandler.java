package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException  ex){
        String message= ex.getMessage();
        if (message.contains("Duplicate entry")){
            // Duplicate entry 'zhangsan' for key 'employee.idx_username' 抛出的异常提示信息
            String[] split=message.split("");
            /* 因为异常信息如上图，使用字符串分割函数来这样分割 */
            String username=split[2];
            /* 一个固定的返回信息 */
            String msg=username+ MessageConstant.ACCOUNT_EXISTS;
            return  Result.error(msg);

        }else {
            return   Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }
}
