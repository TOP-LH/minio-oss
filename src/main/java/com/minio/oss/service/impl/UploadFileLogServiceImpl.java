package com.minio.oss.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.ContentType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minio.oss.common.exception.BusinessException;
import com.minio.oss.domain.UploadFileLog;
import com.minio.oss.domain.dto.ObjectNameDTO;
import com.minio.oss.domain.dto.UploadFileLogDTO;
import com.minio.oss.enums.ContentDispositionEnums;
import com.minio.oss.enums.TargetCategoryEnums;
import com.minio.oss.mapper.UploadFileLogMapper;
import com.minio.oss.service.UploadFileLogService;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author lihao3
 * @date 2022/8/9 13:40
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UploadFileLogServiceImpl extends ServiceImpl<UploadFileLogMapper, UploadFileLog>
    implements UploadFileLogService {

  private static final String PATH_SEPARATOR = "/";

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

  private void putObject(UploadFileLogDTO dto, String objectName) throws Exception {
    if (TargetCategoryEnums.ORGANIZATION.getCode().equalsIgnoreCase(dto.getTargetCategory())) {
      if (ObjectUtil.isEmpty(dto.getDeptCode()) || ObjectUtil.isEmpty(dto.getDeptCode())) {
        throw new BusinessException("部门文件中部门编码和部门名称不能为空!");
      }
    } else if (TargetCategoryEnums.PROJECT.getCode().equalsIgnoreCase(dto.getTargetCategory())) {
      if (ObjectUtil.isEmpty(dto.getProjectCode()) || ObjectUtil.isEmpty(dto.getProjectName())) {
        throw new BusinessException("项目文件中项目编码和项目名称不能为空!");
      }
    }
    this.createBucket(dto.getSourceService());
    InputStream inputStream = dto.getFile().getInputStream();
    minioClient.putObject(
        PutObjectArgs.builder()
            .bucket(dto.getSourceService())
            .contentType(dto.getFile().getContentType())
            .object(objectName)
            .stream(inputStream, dto.getFile().getSize(), -1)
            .build());
    inputStream.close();
  }

  @Override
  public Boolean bucketExists(String bucketName) throws Exception {
    return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
  }

  @Override
  public void createBucket(String bucketName) throws Exception {
    if (!this.bucketExists(bucketName)) {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
    }
  }

  @Override
  public String getObjectName(ObjectNameDTO dto) {
    StringBuilder objectName = new StringBuilder();
    objectName.append(dto.getZoneCode()).append(PATH_SEPARATOR);
    objectName
        .append(DateUtil.format(dto.getCreatTime(), DatePattern.NORM_MONTH_PATTERN))
        .append(PATH_SEPARATOR);
    objectName.append(dto.getTargetCategory()).append(PATH_SEPARATOR);
    if (TargetCategoryEnums.ORGANIZATION.getCode().equalsIgnoreCase(dto.getTargetCategory())) {
      objectName.append(dto.getDeptCode()).append(PATH_SEPARATOR);
    } else if (TargetCategoryEnums.PROJECT.getCode().equalsIgnoreCase(dto.getTargetCategory())) {
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
                .zoneCode(dto.getZoneCode())
                .targetCategory(dto.getTargetCategory())
                .deptCode(dto.getDeptCode())
                .projectCode(dto.getProjectCode())
                .firstLevelFolder(dto.getFirstLevelFolder())
                .secondLevelFolder(dto.getSecondLevelFolder())
                .creatTime(createTime)
                .salt(salt)
                .fileName(dto.getFile().getOriginalFilename())
                .build());
    stopWatch.stop();
    stopWatch.start("开始上传");
    try {
      this.putObject(dto, objectName);
    } catch (Exception e) {
      log.error("文件上传失败, 异常信息为:{}", e.getMessage(), e);
      throw new BusinessException("文件上传失败, 请联系IT人员");
    }
    stopWatch.stop();
    stopWatch.start("插入记录表信息");
    // 拷贝基础属性
    UploadFileLog uploadFileLog = BeanUtil.copyProperties(dto, UploadFileLog.class);
    uploadFileLog.setFileName(dto.getFile().getOriginalFilename());
    uploadFileLog.setFileType(dto.getFile().getContentType());
    uploadFileLog.setCreateTime(createTime);
    uploadFileLog.setSalt(salt);
    uploadFileLog.setFileSize(dto.getFile().getSize());
    // 插入信息
    this.save(uploadFileLog);
    stopWatch.stop();
    // 返回附件ID
    log.info(
        "文件上传完毕, 文件名称为:{}, 文件大小为:{}, 执行过程为:{}",
        dto.getFile().getOriginalFilename(),
        DataSizeUtil.format(uploadFileLog.getFileSize()),
        stopWatch.prettyPrint());
    return uploadFileLog.getId();
  }

  @Override
  public String asyncUploadFile(UploadFileLogDTO dto) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start("组装文件路径");
    String salt = RandomUtil.randomString(5);
    Date createTime = new Date();
    String objectName =
        this.getObjectName(
            ObjectNameDTO.builder()
                .zoneCode(dto.getZoneCode())
                .targetCategory(dto.getTargetCategory())
                .deptCode(dto.getDeptCode())
                .projectCode(dto.getProjectCode())
                .firstLevelFolder(dto.getFirstLevelFolder())
                .secondLevelFolder(dto.getSecondLevelFolder())
                .creatTime(createTime)
                .salt(salt)
                .fileName(dto.getFile().getOriginalFilename())
                .build());
    stopWatch.stop();
    stopWatch.start("开始上传");
    CompletableFuture.runAsync(
        () -> {
          try {
            this.putObject(dto, objectName);
          } catch (Exception e) {
            log.error("文件上传失败, 异常信息为:{}", e.getMessage(), e);
            throw new BusinessException("文件上传失败, 请联系IT人员");
          }
        });
    stopWatch.stop();
    stopWatch.start("插入记录表信息");
    // 拷贝基础属性
    UploadFileLog uploadFileLog = BeanUtil.copyProperties(dto, UploadFileLog.class);
    uploadFileLog.setFileName(dto.getFile().getOriginalFilename());
    uploadFileLog.setFileType(dto.getFile().getContentType());
    uploadFileLog.setCreateTime(createTime);
    uploadFileLog.setSalt(salt);
    uploadFileLog.setFileSize(dto.getFile().getSize());
    // 插入信息
    this.save(uploadFileLog);
    stopWatch.stop();
    // 返回附件ID
    log.info(
        "文件上传完毕, 文件名称为:{}, 文件大小为:{}, 执行过程为:{}",
        dto.getFile().getOriginalFilename(),
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
    try (InputStream object =
            minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(uploadFileLog.getSourceService())
                    .object(objectName)
                    .build());
        ServletOutputStream outputStream = response.getOutputStream()) {
      if (Boolean.FALSE.equals(preview)) {
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
      IoUtil.copy(object, response.getOutputStream());
      outputStream.flush();
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
    stopWatch.start("整理ZIP包");
    // 被压缩文件InputStream
    InputStream[] inputStreams = new InputStream[uploadFileLogList.size()];
    // 被压缩文件名称
    String[] fileNames = new String[uploadFileLogList.size()];
    try {
      for (int i = 0; i < uploadFileLogList.size(); i++) {
        UploadFileLog item = uploadFileLogList.get(i);
        String objectName =
            this.getObjectName(
                ObjectNameDTO.builder()
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
                GetObjectArgs.builder().bucket(item.getSourceService()).object(objectName).build());
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
