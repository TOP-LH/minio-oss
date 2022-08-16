package com.minio.oss;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@MapperScan("com.minio.oss.mapper")
@RefreshScope
@EnableDiscoveryClient
@SpringBootApplication
public class MinioOssApplication {

  public static void main(String[] args) {
    SpringApplication.run(MinioOssApplication.class, args);
  }
}
