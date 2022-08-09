package com.wison.oss;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@MapperScan("com.wison.oss.mapper")
@RefreshScope
@EnableDiscoveryClient
@SpringBootApplication
public class WisonOssApplication {

  public static void main(String[] args) {
    SpringApplication.run(WisonOssApplication.class, args);
  }
}
