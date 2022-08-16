package com.minio.oss.common.emums;

/**
 * 返回信息
 *
 * @author lihao3
 */
public interface ResultCodeService {

  /**
   * 返回码
   *
   * @return Integer
   */
  String getCode();

  /**
   * 返回消息
   *
   * @return String
   */
  String getMessage();
}
