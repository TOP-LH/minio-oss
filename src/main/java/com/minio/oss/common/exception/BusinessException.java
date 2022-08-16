package com.minio.oss.common.exception;

import com.minio.oss.common.emums.ResultCode;
import com.minio.oss.common.emums.ResultCodeService;
import lombok.Getter;

/**
 * @author lihao3
 * @date 2022/8/16
 */
@Getter
public class BusinessException extends RuntimeException {

  /** 返回CODE */
  private final String code;
  /** 消息内容 */
  private final String message;

  public BusinessException(ResultCodeService resultCode) {
    this.code = resultCode.getCode();
    this.message = resultCode.getMessage();
  }

  public BusinessException(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public BusinessException(String message) {
    this.code = ResultCode.FAILURE.getCode();
    this.message = message;
  }
}
