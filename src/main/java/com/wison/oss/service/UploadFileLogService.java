package com.wison.oss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wison.oss.domain.UploadFileLog;
import com.wison.oss.domain.dto.UploadFileLogDTO;

/**
 * @author lihao3
 * @date 2022/8/9 13:40
 */
public interface UploadFileLogService extends IService<UploadFileLog> {

  /**
   * 上传文件
   *
   * @param dto
   * @return
   */
  String uploadFile(UploadFileLogDTO dto);

  /**
   * 获取文件路径
   *
   * @param zoneCode
   * @param targetCategory
   * @param deptCode
   * @param projectCode
   * @param firstLevelFolder
   * @param secondLevelFolder
   * @param fileName
   * @return
   */
  String getObjectName(
      String zoneCode,
      String targetCategory,
      String deptCode,
      String projectCode,
      String firstLevelFolder,
      String secondLevelFolder,
      String fileName);

  /**
   * 下载文件
   *
   * @param id
   */
  void downloadFile(String id);
}
