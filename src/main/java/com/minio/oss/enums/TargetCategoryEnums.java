package com.minio.oss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lihao3
 * @date 2022/3/11 13:13
 */
@Getter
@AllArgsConstructor
public enum TargetCategoryEnums {

  /** 项目文件 */
  PROJECT("Project"),
  /** 部门文件 */
  ORGANIZATION("Organization"),
  ;

  private final String code;
}
