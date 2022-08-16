package com.minio.oss.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
  private String sourceService;
  /** 来源系统ID */
  private String sourceKey;
  /** 创建人登录名 */
  private String loginCode;
  /** 板块编码 */
  private String zoneCode;
  /** 板块名称 */
  private String zoneName;
  /** 文件分类(部门文件还是项目文件) */
  private String targetCategory;
  /** 带盐值 */
  private String salt;
  /** 部门编码 */
  private String deptCode;
  /** 部门名称 */
  private String deptName;
  /** 项目编码 */
  private String projectCode;
  /** 项目名称 */
  private String projectName;
  /** 用户自定义文件名称 */
  private String customName;
  /** 原始文件名称 */
  private String fileName;
  /** 文件类型 */
  private String fileType;
  /** 文件URL */
  private Long fileSize;
  /** 一级文件夹名称 */
  private String firstLevelFolder;
  /** 二级文件夹名称 */
  private String secondLevelFolder;
  /** 备注 */
  private String remark;
  /** 创建时间 */
  private Date createTime;
  /** 逻辑删除 */
  @TableLogic(value = "false", delval = "true")
  private Boolean deleteFlag;
}
