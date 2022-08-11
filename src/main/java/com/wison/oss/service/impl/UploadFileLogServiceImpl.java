package com.wison.oss.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.ContentType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wison.base.exception.BusinessException;
import com.wison.oss.domain.UploadFileLog;
import com.wison.oss.domain.dto.ObjectNameDTO;
import com.wison.oss.domain.dto.UploadFileLogDTO;
import com.wison.oss.enums.ContentDispositionEnums;
import com.wison.oss.enums.TargetCategoryEnums;
import com.wison.oss.mapper.UploadFileLogMapper;
import com.wison.oss.properties.MinioAutoProperties;
import com.wison.oss.service.UploadFileLogService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

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

  private final MinioAutoProperties minioProperties;
  private final MinioClient minioClient;

  private static void initResponse(
      String fileName,
      String contentType,
      ContentDispositionEnums contentDispositionEnums,
      HttpServletResponse response)
      throws UnsupportedEncodingException {
    response.reset();
    response.setHeader(
        "Content-Disposition",
        StrUtil.format(
            "{};filename={}",
            contentDispositionEnums.getCode(),
            URLEncoder.encode(fileName, "UTF-8")));
    response.setContentType(contentType);
    response.setCharacterEncoding("utf-8");
  }

  @Override
  public String getObjectName(ObjectNameDTO dto) {
    StringBuilder objectName = new StringBuilder();
    objectName.append(dto.getSourceService()).append(PATH_SEPARATOR);
    objectName.append(dto.getZoneCode()).append(PATH_SEPARATOR);
    objectName
        .append(DateUtil.format(dto.getCreatTime(), DatePattern.NORM_MONTH_PATTERN))
        .append(PATH_SEPARATOR);
    objectName.append(dto.getTargetCategory()).append(PATH_SEPARATOR);
    if (TargetCategoryEnums.ORGANIZATION.getCode().equals(dto.getTargetCategory())) {
      objectName.append(dto.getDeptCode()).append(PATH_SEPARATOR);
    } else if (TargetCategoryEnums.PROJECT.getCode().equals(dto.getTargetCategory())) {
      objectName.append(dto.getProjectCode()).append(PATH_SEPARATOR);
    } else {
      throw new BusinessException("文件类型异常");
    }
    objectName.append(dto.getFirstLevelFolder()).append(PATH_SEPARATOR);
    objectName.append(dto.getSecondLevelFolder()).append(PATH_SEPARATOR);
    objectName
        .append(FileNameUtil.getPrefix(dto.getFileName()))
        .append("_")
        .append(dto.getSalt())
        .append(".")
        .append(FileNameUtil.getSuffix(dto.getFileName()));
    return objectName.toString();
  }

  @Override
  public String uploadFile(UploadFileLogDTO dto) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start("组装文件路径");
    String salt = RandomUtil.randomString(5);
    Date createTime = new Date();
    String objectName =
        this.getObjectName(
            ObjectNameDTO.builder()
                .sourceService(dto.getSourceService())
                .zoneCode(dto.getZoneCode())
                .targetCategory(dto.getTargetCategory())
                .deptCode(dto.getDeptCode())
                .projectCode(dto.getProjectCode())
                .firstLevelFolder(dto.getFirstLevelFolder())
                .secondLevelFolder(dto.getSecondLevelFolder())
                .creatTime(createTime)
                .salt(salt)
                .fileName(dto.getMultipartFile().getOriginalFilename())
                .build());
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
    uploadFileLog.setCreateTime(createTime);
    uploadFileLog.setSalt(salt);
    uploadFileLog.setFileSize(dto.getMultipartFile().getSize());
    // 插入信息
    this.save(uploadFileLog);
    stopWatch.stop();
    // 返回附件ID
    log.info(
        "文件上传完毕, 文件名称为:{}, 文件大小为:{}, 执行过程为:{}",
        dto.getMultipartFile().getOriginalFilename(),
        DataSizeUtil.format(uploadFileLog.getFileSize()),
        stopWatch.prettyPrint());
    return uploadFileLog.getId();
  }

  @Override
  public void downloadFile(String id, Boolean preview, HttpServletResponse response) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start("根据ID查询文件记录");
    UploadFileLog uploadFileLog = this.getById(id);
    if (ObjectUtil.isEmpty(uploadFileLog)) {
      throw new BusinessException("文件ID不存在");
    }
    stopWatch.stop();
    stopWatch.start("组装objectName");
    String objectName =
        this.getObjectName(
            ObjectNameDTO.builder()
                .sourceService(uploadFileLog.getSourceService())
                .zoneCode(uploadFileLog.getZoneCode())
                .targetCategory(uploadFileLog.getTargetCategory())
                .deptCode(uploadFileLog.getDeptCode())
                .projectCode(uploadFileLog.getProjectCode())
                .firstLevelFolder(uploadFileLog.getFirstLevelFolder())
                .secondLevelFolder(uploadFileLog.getSecondLevelFolder())
                .creatTime(uploadFileLog.getCreateTime())
                .salt(uploadFileLog.getSalt())
                .fileName(uploadFileLog.getFileName())
                .build());
    stopWatch.stop();
    stopWatch.start("下载文件");
    try {
      InputStream object =
          minioClient.getObject(
              GetObjectArgs.builder().bucket(BUCKET_NAME).object(objectName).build());
      byte[] bytes = new byte[1024];
      int length;
      if (ObjectUtil.isEmpty(preview) || Boolean.FALSE.equals(preview)) {
        initResponse(
            uploadFileLog.getCustomName(),
            ContentType.OCTET_STREAM.toString(),
            ContentDispositionEnums.ATTACHMENT,
            response);
      } else {
        initResponse(
            uploadFileLog.getCustomName(),
            uploadFileLog.getFileType(),
            ContentDispositionEnums.INLINE,
            response);
      }
      OutputStream outputStream = response.getOutputStream();
      while ((length = object.read(bytes)) > 0) {
        outputStream.write(bytes, 0, length);
      }
      outputStream.close();
      stopWatch.stop();
      log.info(
          "文件下载成功, 文件名称为:{}, 文件大小为:{}, 执行过程为:{}",
          uploadFileLog.getFileName(),
          DataSizeUtil.format(uploadFileLog.getFileSize()),
          stopWatch.prettyPrint());
    } catch (Exception e) {
      log.error("文件下载失败, 异常信息为:{}", e.getMessage(), e);
      throw new BusinessException("文件下载失败, 请联系IT人员");
    }
  }

  @Override
  public void downloadZipFile(List<String> idList, String zipName, HttpServletResponse response) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start("根据ID查询文件记录");
    List<UploadFileLog> uploadFileLogList = this.listByIds(idList);
    if (ObjectUtil.isEmpty(uploadFileLogList)) {
      throw new BusinessException("文件ID不存在");
    }
    stopWatch.stop();
    try {
      stopWatch.start("整理ZIP包");
      // 被压缩文件InputStream
      InputStream[] inputStreams = new InputStream[uploadFileLogList.size()];
      // 被压缩文件名称
      String[] fileNames = new String[uploadFileLogList.size()];
      for (int i = 0; i < uploadFileLogList.size(); i++) {
        UploadFileLog item = uploadFileLogList.get(i);
        String objectName =
            this.getObjectName(
                ObjectNameDTO.builder()
                    .sourceService(item.getSourceService())
                    .zoneCode(item.getZoneCode())
                    .targetCategory(item.getTargetCategory())
                    .deptCode(item.getDeptCode())
                    .projectCode(item.getProjectCode())
                    .firstLevelFolder(item.getFirstLevelFolder())
                    .secondLevelFolder(item.getSecondLevelFolder())
                    .creatTime(item.getCreateTime())
                    .salt(item.getSalt())
                    .fileName(item.getFileName())
                    .build());
        // 获取文件
        InputStream object =
            minioClient.getObject(
                GetObjectArgs.builder().bucket(BUCKET_NAME).object(objectName).build());
        inputStreams[i] = object;
        fileNames[i] =
            FileNameUtil.getPrefix(item.getCustomName())
                + "_"
                + item.getSalt()
                + "."
                + FileNameUtil.getSuffix(item.getCustomName());
      }
      stopWatch.stop();
      initResponse(
          zipName + ".zip",
          ContentType.OCTET_STREAM.toString(),
          ContentDispositionEnums.ATTACHMENT,
          response);
      stopWatch.start("下载ZIP包");
      ZipUtil.zip(response.getOutputStream(), fileNames, inputStreams);
      stopWatch.stop();
    } catch (Exception e) {
      log.error("文件下载失败, 异常信息为:{}", e.getMessage(), e);
      throw new BusinessException("文件下载失败, 请联系IT人员");
    }
  }

  @Override
  public void deleteById(String id) {
    this.removeById(id);
  }

  @Override
  public void deleteBySourceService(String sourceService, String sourceKey) {
    this.remove(
        new LambdaQueryWrapper<UploadFileLog>()
            .eq(UploadFileLog::getSourceService, sourceService)
            .eq(UploadFileLog::getSourceKey, sourceKey));
  }
}
