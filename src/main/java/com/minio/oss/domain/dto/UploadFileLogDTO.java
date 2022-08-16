package com.minio.oss.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 上传文件记录表
 *
 * @author lihao3
 * @date 2022/8/9 13:40
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("上传文件实体类")
public class UploadFileLogDTO implements Serializable {

  /** 来源系统名称 */
  @NotEmpty(message = "来源系统名称不可为空!")
  @ApiModelProperty(value = "来源系统名称", required = true)
  private String sourceService;
  /** 来源系统ID */
  @ApiModelProperty("来源系统ID")
  private String sourceKey;
  /** 创建人登录名 */
  @NotEmpty(message = "创建人唯一ID不可为空!")
  @ApiModelProperty(value = "创建人唯一ID", required = true)
  private String loginCode;
  /** 板块编码 */
  @NotEmpty(message = "板块编码不可为空!")
  @ApiModelProperty(value = "板块编码", required = true)
  private String zoneCode;
  /** 板块名称 */
  @NotEmpty(message = "板块名称不可为空!")
  @ApiModelProperty(value = "板块名称", required = true)
  private String zoneName;
  /** 文件分类(部门文件还是项目文件) */
  @NotEmpty(message = "文件分类不可为空!")
  @ApiModelProperty(value = "文件分类(部门文件还是项目文件)", required = true)
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
  /** 文件 */
  @NotNull(message = "文件不可为空!")
  @ApiModelProperty("文件")
  private MultipartFile file;
  /** 用户自定义文件名称 */
  @ApiModelProperty("用户自定义文件名称(带文件后缀)")
  private String customName;
  /** 一级文件夹名称 */
  @NotNull(message = "一级文件夹名称不可为空!")
  @ApiModelProperty(value = "一级文件夹名称", required = true)
  private String firstLevelFolder;
  /** 二级文件夹名称 */
  @NotNull(message = "二级文件夹名称不可为空!")
  @ApiModelProperty(value = "二级文件夹名称", required = true)
  private String secondLevelFolder;
  /** 备注 */
  @ApiModelProperty("备注")
  private String remark;
}
