package com.minio.oss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lihao3
 * @date 2022/8/10 13:22
 */
@Getter
@AllArgsConstructor
public enum ContentDispositionEnums {

  /** 预览 */
  INLINE("inline"),
  /** 下载 */
  ATTACHMENT("attachment");

  private final String code;
}
