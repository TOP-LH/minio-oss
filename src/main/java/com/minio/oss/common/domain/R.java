package com.minio.oss.common.domain;

import com.github.pagehelper.Page;
import com.minio.oss.common.emums.ResultCode;
import com.minio.oss.common.emums.ResultCodeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author lihao3
 * @date 2022/8/16
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class R<T> implements Serializable {

  /** 携 带 消 息 */
  private String msg;
  /** 状 态 码 */
  private String code;
  /** 携 带 消 息 */
  private T data;
  /** 总条数 */
  private long totalNumber;
  /** 是 否 成 功 */
  private boolean success;
  /** 操 作 时 间 */
  private long timeStamp;

  /**
   * 不返回任何信息的成功
   *
   * @param <T>
   * @return
   */
  public static <T> R<T> success() {
    return new R<T>(
        ResultCode.SUCCESS.getMessage(),
        ResultCode.SUCCESS.getCode(),
        null,
        0,
        true,
        System.currentTimeMillis());
  }

  /**
   * 添加返回语的成功
   *
   * @param msg
   * @param <T>
   * @return
   */
  public static <T> R<T> success(String msg) {
    return new R<T>(msg, ResultCode.SUCCESS.getCode(), null, 0, true, System.currentTimeMillis());
  }

  /**
   * 添加返回语句和返回数据的成功
   *
   * @param msg
   * @param data
   * @param <T>
   * @return
   */
  public static <T> R<T> success(String msg, T data) {
    return new R<T>(msg, ResultCode.SUCCESS.getCode(), data, 0, true, System.currentTimeMillis());
  }

  /**
   * 分页查询版-添加返回语句和返回数据的成功
   *
   * @param msg
   * @param page
   * @param <T>
   * @return
   */
  public static <T> R<T> success(String msg, Page<T> page) {
    return new R<T>(
        msg,
        ResultCode.SUCCESS.getCode(),
        (T) page.getResult(),
        page.getTotal(),
        true,
        System.currentTimeMillis());
  }

  /**
   * 错误返回
   *
   * @param resultCode
   * @return
   */
  public static R error(ResultCodeService resultCode) {
    return new R(
        resultCode.getMessage(), resultCode.getCode(), null, 0, false, System.currentTimeMillis());
  }

  /**
   * 错误返回
   *
   * @param msg
   * @param code
   * @return
   */
  public static R error(String msg, String code) {
    return new R(msg, code, null, 0, false, System.currentTimeMillis());
  }
}
