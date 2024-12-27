package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation(value = "上传文件")
    public Result<String> upload(MultipartFile file)  {
        log.info("上传文件{}...",file.getOriginalFilename());
        try {
            String originalFilename = file.getOriginalFilename();
            String substring = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String objectName = UUID.randomUUID() + substring;

            String imageAddress = aliOssUtil.upload(file.getBytes(), objectName);

            return Result.success(imageAddress);
        } catch (IOException e) {
            log.error("文件上传失败");
        }

        return Result.error("文件上传失败");
    }

}
