package com.minio.oss.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 查询实体类
 *
 * @author lihao3
 * @date 2022/8/9 14:02
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("查询实体类")
public class UploadFileLogQuery implements Serializable {

  /** 唯一ID */
  @ApiModelProperty("唯一ID")
  private String id;
  /** 来源系统名称 */
  @ApiModelProperty("来源系统名称")
  private String sourceService;
  /** 来源系统ID */
  @ApiModelProperty("来源系统ID")
  private String sourceKey;
  /** 创建人登录名 */
  @ApiModelProperty("创建人登录名")
  private String loginCode;
  /** 板块编码 */
  @ApiModelProperty("板块编码")
  private String zoneCode;
  /** 板块名称 */
  @ApiModelProperty("板块名称")
  private String zoneName;
  /** 文件分类(部门文件还是项目文件) */
  @ApiModelProperty("文件分类(部门文件还是项目文件)")
  private String targetCategory;
  /** 部门编码 */
  @ApiModelProperty("部门编码")
  private String deptCode;
  /** 部门名称 */
  @ApiModelProperty("部门名称")
  private String deptName;
  /** 项目编码 */
  @ApiModelProperty("项目编码")
  private String projectCode;
  /** 项目名称 */
  @ApiModelProperty("项目名称")
  private String projectName;
  /** 用户自定义文件名称 */
  @ApiModelProperty("用户自定义文件名称")
  private String customName;
  /** 原始文件名称 */
  @ApiModelProperty("原始文件名称")
  private String fileName;
  /** 文件类型 */
  @ApiModelProperty("文件类型")
  private String fileType;
  /** 一级文件夹名称 */
  @ApiModelProperty("一级文件夹名称")
  private String firstLevelFolder;
  /** 二级文件夹名称 */
  @ApiModelProperty("二级文件夹名称")
  private String secondLevelFolder;
  /** 备注 */
  @ApiModelProperty("备注")
  private String remark;
  /** 开始时间 */
  @ApiModelProperty("开始时间")
  private String startTime;
  /** 截止时间 */
  @ApiModelProperty("截止时间")
  private String endTime;
}
