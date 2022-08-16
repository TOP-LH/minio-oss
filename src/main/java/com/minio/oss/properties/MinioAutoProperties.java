package com.minio.oss.properties;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * @author lihao3
 * @date 2022/8/9 14:17
 */
@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioAutoProperties {

  /** 服务地址 */
  @NotEmpty(message = "minio服务地址不可为空")
  @URL(message = "minio服务地址格式错误")
  private String url;
  /** 认证账户 */
  @NotEmpty(message = "minio认证账户不可为空")
  private String accessKey;
  /** 认证密码 */
  @NotEmpty(message = "minio认证密码不可为空")
  private String secretKey;
  /** 设置HTTP连接、写入和读取超时。值为0意味着没有超时 HTTP连接超时，以毫秒为单位。 */
  @DurationUnit(ChronoUnit.MILLIS)
  private Duration connectTimeout = Duration.ofMillis(0);
  /** 设置HTTP连接、写入和读取超时。值为0意味着没有超时 HTTP写超时，以毫秒为单位。 */
  @DurationUnit(ChronoUnit.MILLIS)
  private Duration writeTimeout = Duration.ofMillis(0);
  /** 设置HTTP连接、写入和读取超时。值为0意味着没有超时 HTTP读取超时，以毫秒为单位。 */
  @DurationUnit(ChronoUnit.MILLIS)
  private Duration readTimeout = Duration.ofMillis(0);
}
