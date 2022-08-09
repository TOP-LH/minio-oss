package com.wison.oss.controller;

import com.wison.base.domain.Result;
import com.wison.oss.domain.dto.UploadFileLogDTO;
import com.wison.oss.domain.query.UploadFileLogQuery;
import com.wison.oss.service.UploadFileLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public Result uploadFile(UploadFileLogDTO dto) {}

  @DeleteMapping("{id}")
  @ApiOperation("根据文件唯一ID删除")
  public Result deleteById(@PathVariable String id) {}

  @DeleteMapping("{sourceService}/{sourceKey}")
  @ApiOperation("根据来源系统和来源系统ID删除")
  public Result deleteBySourceService(
      @PathVariable String sourceService, @PathVariable String sourceKey) {}

  @PutMapping("input/source-key")
  @ApiOperation("填充来源ID")
  public Result inputSourceKey(UploadFileLogDTO dto) {}

  @GetMapping("upload/file/log/list")
  @ApiOperation("查询附件列表")
  public Result selectUploadFileLogList(@ModelAttribute UploadFileLogQuery query) {}

  @GetMapping("upload/file/log/page/list")
  @ApiOperation("分页查询附件列表")
  public Result selectUploadFileLogPageList(@ModelAttribute UploadFileLogQuery query) {}
}