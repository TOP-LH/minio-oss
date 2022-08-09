package com.wison.oss.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wison.oss.domain.UploadFileLog;
import com.wison.oss.mapper.UploadFileLogMapper;
import com.wison.oss.service.UploadFileLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
/**
 * @author lihao3
 * @date 2022/8/9 13:40
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UploadFileLogServiceImpl extends ServiceImpl<UploadFileLogMapper, UploadFileLog>
    implements UploadFileLogService {}
