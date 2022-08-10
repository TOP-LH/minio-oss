package com.wison.oss.domain.dto;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

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
  /** 文件 */
  @ApiModelProperty("文件")
  @JSONField(serialize = false)
  private MultipartFile multipartFile;
  /** 用户自定义文件名称 */
  @ApiModelProperty("用户自定义文件名称(带文件后缀)")
  private String customName;
  /** 一级文件夹名称 */
  @ApiModelProperty("一级文件夹名称")
  private String firstLevelFolder;
  /** 二级文件夹名称 */
  @ApiModelProperty("二级文件夹名称")
  private String secondLevelFolder;
  /** 备注 */
  @ApiModelProperty("备注")
  private String remark;
}
