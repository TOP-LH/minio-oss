package com.minio.oss.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author lihao3
 * @date 2022/8/11 9:18
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ObjectNameDTO {

  /** 板块编码 */
  private String zoneCode;
  /** 文件分类(部门文件还是项目文件) */
  private String targetCategory;
  /** 部门编码 */
  private String deptCode;
  /** 项目编码 */
  private String projectCode;
  /** 一级文件夹名称 */
  private String firstLevelFolder;
  /** 二级文件夹名称 */
  private String secondLevelFolder;
  /** 创建时间 */
  private Date creatTime;
  /** 带盐值 */
  private String salt;
  /** 原始文件名称 */
  private String fileName;
}
