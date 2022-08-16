package com.minio.oss;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.minio.oss.mapper")
@SpringBootApplication
public class MinioOssApplication {

  public static void main(String[] args) {
    SpringApplication.run(MinioOssApplication.class, args);
  }
}
