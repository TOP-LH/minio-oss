package com.minio.oss.common.handler;

import com.minio.oss.common.domain.R;
import com.minio.oss.common.emums.ResultCode;
import com.minio.oss.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author lihao3
 * @date 2022/8/16
 */
@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

  /** 自定义异常抛出 */
  @ExceptionHandler(BusinessException.class)
  public R businessExceptionHandler(BusinessException e) {
    log.warn(e.getMessage(), e);
    return R.error(e.getCode(), e.getMessage());
  }

  /** 自定义验证异常 */
  @ExceptionHandler(BindException.class)
  public R validatedBindException(BindException e) {
    log.warn(e.getMessage(), e);
    String message = e.getAllErrors().get(0).getDefaultMessage();
    return R.error(ResultCode.FAILURE.getCode(), message);
  }

  /** 自定义验证异常 */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public R validExceptionHandler(MethodArgumentNotValidException e) {
    log.warn(e.getMessage(), e);
    String message = e.getBindingResult().getFieldError().getDefaultMessage();
    return R.error(ResultCode.FAILURE.getCode(), message);
  }

  /** 未知异常抛出 */
  @ExceptionHandler(Exception.class)
  public R handleException(Exception e) {
    log.error(e.getMessage(), e);
    return R.error(ResultCode.FAILURE);
  }
}
