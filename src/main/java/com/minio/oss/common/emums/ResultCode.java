package com.minio.oss.common.emums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回枚举类
 *
 * @author 24709
 */
@Getter
@AllArgsConstructor
public enum ResultCode implements ResultCodeService {
  SUCCESS("200", "成功"),
  FAILURE("500", "系统繁忙，请稍候再试!"),
  ;

  /** 返回CODE */
  private final String code;
  /** 消息内容 */
  private final String message;
}
