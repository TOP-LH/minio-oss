package com.wison.oss.controller;

import cn.hutool.core.util.ObjectUtil;
import com.wison.base.domain.Result;
import com.wison.oss.domain.dto.UploadFileLogDTO;
import com.wison.oss.service.UploadFileLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;

/**
 * @author lihao3
 * @date 2022/8/9 13:50
 */
@Api(tags = "文件管理接口")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class OssController {

  private final UploadFileLogService uploadFileLogService;

  @PostMapping("upload/file")
  @ApiOperation("上传文件")
  public Result uploadFile(@Validated UploadFileLogDTO dto) {
    if (ObjectUtil.isEmpty(dto.getCustomName())) {
      dto.setCustomName(dto.getFile().getOriginalFilename());
    }
    String fileId = uploadFileLogService.uploadFile(dto);
    return Result.success("上传成功!", fileId);
  }

  @PostMapping("async/upload/file")
  @ApiOperation("异步上传文件")
  public Result asyncUploadFile(@Validated UploadFileLogDTO dto) {
    if (ObjectUtil.isEmpty(dto.getCustomName())) {
      dto.setCustomName(dto.getFile().getOriginalFilename());
    }
    String fileId = uploadFileLogService.asyncUploadFile(dto);
    return Result.success("上传成功!", fileId);
  }

  @GetMapping("download/file")
  @ApiOperation("下载文件")
  public void downloadFile(
      @ApiParam("文件ID") @NotEmpty(message = "文件ID不可为空") String id,
      @ApiParam("是否预览") Boolean preview,
      HttpServletResponse response) {
    if (ObjectUtil.isEmpty(preview)) {
      preview = Boolean.FALSE;
    }
    uploadFileLogService.downloadFile(id, preview, response);
  }

  @GetMapping("download/zip/file")
  @ApiOperation("多文件打包下载")
  public void uploadTarFile(
      @ApiParam("文件ID") @NotEmpty(message = "文件ID不可为空") String ids,
      @ApiParam("zip包名称(不带文件后缀)") String zipName,
      HttpServletResponse response) {
    List<String> idList = Arrays.asList(ids.split(","));
    if (ObjectUtil.isEmpty(zipName)) {
      zipName = String.valueOf(System.currentTimeMillis());
    }
    uploadFileLogService.downloadZipFile(idList, zipName, response);
  }

  @DeleteMapping("delete/file")
  @ApiOperation("根据文件唯一ID删除")
  public Result deleteById(@ApiParam("文件ID") @NotEmpty(message = "文件ID不可为空") String id) {
    uploadFileLogService.deleteById(id);
    return Result.success("删除成功!");
  }
}
