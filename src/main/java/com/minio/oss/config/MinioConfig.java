package com.minio.oss.config;

import com.minio.oss.properties.MinioAutoProperties;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lihao3
 * @date 2022/8/9 14:20
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MinioConfig {

  private final MinioAutoProperties minioAutoProperties;

  @Bean
  public MinioClient minioClient() {
    log.info(
        "开始初始化MinioClient, url为{}, accessKey为:{}",
        minioAutoProperties.getUrl(),
        minioAutoProperties.getAccessKey());
    MinioClient minioClient =
        MinioClient.builder()
            .endpoint(minioAutoProperties.getUrl())
            .credentials(minioAutoProperties.getAccessKey(), minioAutoProperties.getSecretKey())
            .build();
    minioClient.setTimeout(
        minioAutoProperties.getConnectTimeout().toMillis(),
        minioAutoProperties.getWriteTimeout().toMillis(),
        minioAutoProperties.getReadTimeout().toMillis());
    log.info("MinioClient初始化成功!");
    return minioClient;
  }
}
