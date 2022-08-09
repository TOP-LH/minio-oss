package com.wison.oss.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 上传文件记录表
 *
 * @author lihao3
 * @date 2022/8/9 13:40
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "upload_file_log")
public class UploadFileLog implements Serializable {

  /** 分布式唯一ID */
  @TableId(value = "id", type = IdType.ASSIGN_ID)
  private String id;
  /** 来源系统名称 */
  @TableField(value = "source_service")
  private String sourceService;
  /** 来源系统ID */
  @TableField(value = "source_key")
  private String sourceKey;
  /** 创建人登录名 */
  @TableField(value = "login_code")
  private String loginCode;
  /** 板块编码 */
  @TableField(value = "zone_code")
  private String zoneCode;
  /** 板块名称 */
  @TableField(value = "zone_name")
  private String zoneName;
  /** 文件分类(部门文件还是项目文件) */
  @TableField(value = "target_category")
  private String targetCategory;
  /** 部门编码 */
  @TableField(value = "dept_code")
  private String deptCode;
  /** 部门名称 */
  @TableField(value = "dept_name")
  private String deptName;
  /** 项目编码 */
  @TableField(value = "project_code")
  private String projectCode;
  /** 项目名称 */
  @TableField(value = "project_name")
  private String projectName;
  /** 用户自定义文件名称 */
  @TableField(value = "custom_name")
  private String customName;
  /** 原始文件名称 */
  @TableField(value = "file_name")
  private String fileName;
  /** 文件类型 */
  @TableField(value = "file_type")
  private String fileType;
  /** 一级文件夹名称 */
  @TableField(value = "first_level_folder")
  private String firstLevelFolder;
  /** 二级文件夹名称 */
  @TableField(value = "second_level_folder")
  private String secondLevelFolder;
  /** 备注 */
  @TableField(value = "remark")
  private String remark;
  /** 创建时间 */
  @TableField(value = "create_time")
  private Date createTime;
  /** 逻辑删除 */
  @TableField(value = "delete_flag")
  private Boolean deleteFlag;
}
