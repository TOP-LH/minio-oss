package com.minio.oss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minio.oss.domain.UploadFileLog;
import com.minio.oss.domain.dto.ObjectNameDTO;
import com.minio.oss.domain.dto.UploadFileLogDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author lihao3
 * @date 2022/8/9 13:40
 */
public interface UploadFileLogService extends IService<UploadFileLog> {

  /**
   * 判断桶是否存在
   *
   * @param bucketName bucket名称
   * @return true存在，false不存在
   */
  Boolean bucketExists(String bucketName) throws Exception;

  /**
   * 创建bucket
   *
   * @param bucketName bucket名称
   */
  void createBucket(String bucketName) throws Exception;

  /**
   * 上传文件
   *
   * @param dto
   * @return
   */
  String uploadFile(UploadFileLogDTO dto);

  /**
   * 异步上传文件
   *
   * @param dto
   * @return
   */
  String asyncUploadFile(UploadFileLogDTO dto);

  /**
   * 获取文件路径
   *
   * @param dto
   * @return
   */
  String getObjectName(ObjectNameDTO dto);

  /**
   * 下载文件
   *
   * @param id
   * @param preview
   * @param response
   */
  void downloadFile(String id, Boolean preview, HttpServletResponse response);

  /**
   * 多文件下载zip包
   *
   * @param idList
   * @param zipName
   * @param response
   */
  void downloadZipFile(List<String> idList, String zipName, HttpServletResponse response);

  /**
   * 根据ID删除
   *
   * @param id
   */
  void deleteById(String id);

  /**
   * 根据来源系统删除
   *
   * @param sourceService
   * @param sourceKey
   */
  void deleteBySourceService(String sourceService, String sourceKey);
}
