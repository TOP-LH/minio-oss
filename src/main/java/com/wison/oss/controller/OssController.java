package com.wison.oss.controller;

import com.wison.base.domain.Result;
import com.wison.oss.domain.dto.UploadFileLogDTO;
import com.wison.oss.service.UploadFileLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @author lihao3
 * @date 2022/8/9 13:50
 */
@Api(tags = "文件管理接口")
@Slf4j
@RestController
@RequiredArgsConstructor
public class OssController {

  private final UploadFileLogService uploadFileLogService;

  @PostMapping("upload/file")
  @ApiOperation("上传文件")
  public Result uploadFile(UploadFileLogDTO dto) {
    String fileId = uploadFileLogService.uploadFile(dto);
    return Result.success("上传成功!", fileId);
  }

  @GetMapping("download/file")
  @ApiOperation("下载文件")
  public void downloadFile(String id, HttpServletResponse response) {
    uploadFileLogService.downloadFile(id, response);
  }

  @GetMapping("download/zip/file")
  @ApiOperation("上传文件")
  public void uploadTarFile(String ids, String zipName, HttpServletResponse response) {
    List<String> idList = Arrays.asList(ids.split(","));
    uploadFileLogService.downloadZipFile(idList, zipName, response);
  }

  @DeleteMapping("delete")
  @ApiOperation("根据文件唯一ID删除")
  public Result deleteById(String id) {
    uploadFileLogService.deleteById(id);
    return Result.success("删除成功!");
  }
}
