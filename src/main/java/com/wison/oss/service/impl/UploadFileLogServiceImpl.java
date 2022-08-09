package com.wison.oss.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wison.base.exception.BusinessException;
import com.wison.oss.domain.UploadFileLog;
import com.wison.oss.domain.dto.UploadFileLogDTO;
import com.wison.oss.enums.TargetCategoryEnums;
import com.wison.oss.mapper.UploadFileLogMapper;
import com.wison.oss.properties.MinioAutoProperties;
import com.wison.oss.service.UploadFileLogService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * @author lihao3
 * @date 2022/8/9 13:40
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UploadFileLogServiceImpl extends ServiceImpl<UploadFileLogMapper, UploadFileLog>
    implements UploadFileLogService {

  private static final String BUCKET_NAME = "wison-oss";
  private static final String PATH_SEPARATOR = "/";
  private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake();

  private final MinioAutoProperties minioProperties;
  private final MinioClient minioClient;

  @Override
  public String uploadFile(UploadFileLogDTO dto) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start("组装文件路径");
    String objectName =
        this.getObjectName(
            dto.getZoneCode(),
            dto.getTargetCategory(),
            dto.getDeptCode(),
            dto.getProjectCode(),
            dto.getFirstLevelFolder(),
            dto.getSecondLevelFolder(),
            dto.getMultipartFile().getOriginalFilename());
    stopWatch.stop();
    stopWatch.start("开始上传");
    try {
      InputStream inputStream = dto.getMultipartFile().getInputStream();
      minioClient.putObject(
          PutObjectArgs.builder()
              .bucket(BUCKET_NAME)
              .contentType(dto.getMultipartFile().getContentType())
              .object(objectName)
              .stream(inputStream, dto.getMultipartFile().getSize(), -1)
              .build());
    } catch (Exception e) {
      log.error("文件上传失败, 异常信息为:{}", e.getMessage(), e);
      throw new BusinessException("文件上传失败, 请联系IT人员");
    }
    stopWatch.stop();
    stopWatch.start("插入记录表信息");
    // 拷贝基础属性
    String fileUrl =
        minioProperties.getUrl() + PATH_SEPARATOR + BUCKET_NAME + PATH_SEPARATOR + objectName;
    UploadFileLog uploadFileLog = BeanUtil.copyProperties(dto, UploadFileLog.class);
    uploadFileLog.setFileName(dto.getMultipartFile().getOriginalFilename());
    uploadFileLog.setFileType(dto.getMultipartFile().getContentType());
    uploadFileLog.setFileUrl(fileUrl);
    // 插入信息
    this.save(uploadFileLog);
    stopWatch.stop();
    // 返回附件ID
    log.info(
        "文件上传完毕, 文件名称为:{}, 文件大小为:{}, 执行过程为:{}",
        dto.getMultipartFile().getOriginalFilename(),
        dto.getMultipartFile().getSize(),
        stopWatch.prettyPrint());
    return uploadFileLog.getId();
  }

  @Override
  public String getObjectName(
      String zoneCode,
      String targetCategory,
      String deptCode,
      String projectCode,
      String firstLevelFolder,
      String secondLevelFolder,
      String fileName) {
    StringBuilder objectName = new StringBuilder();
    objectName.append(zoneCode).append(PATH_SEPARATOR);
    if (TargetCategoryEnums.ORGANIZATION.getCode().equals(targetCategory)) {
      objectName.append(deptCode).append(PATH_SEPARATOR);
    } else if (TargetCategoryEnums.PROJECT.getCode().equals(targetCategory)) {
      objectName.append(projectCode).append(PATH_SEPARATOR);
    } else {
      throw new BusinessException("文件类型异常");
    }
    objectName.append(firstLevelFolder).append(PATH_SEPARATOR);
    objectName.append(secondLevelFolder).append(PATH_SEPARATOR);
    objectName.append(fileName);
    return objectName.toString();
  }

  @Override
  public void downloadFile(String id) {
    HttpServletResponse response =
        ((ServletRequestAttributes)
                Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
            .getResponse();
    StopWatch stopWatch = new StopWatch();
    stopWatch.start("根据ID查询文件记录");
    UploadFileLog uploadFileLog = this.getById(id);
    if (ObjectUtil.isEmpty(uploadFileLog)) {
      throw new BusinessException("文件ID不存在");
    }
    stopWatch.stop();
    try {
      // response.setHeader("content-type", "application/octet-stream");
      // response.setContentType("application/octet-stream");
      // 下载文件能正常显示中文
      response.setHeader(
          "Content-Disposition",
          "attachment;filename=" + URLEncoder.encode(uploadFileLog.getFileName(), "UTF-8"));
      HttpUtil.download(uploadFileLog.getFileUrl(), response.getOutputStream(), true);
    } catch (Exception e) {
      log.error("文件下载失败, 异常信息为:{}", e.getMessage(), e);
      throw new BusinessException("文件下载失败, 请联系IT人员");
    }
  }
}
