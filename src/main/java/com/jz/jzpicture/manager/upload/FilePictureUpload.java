package com.jz.jzpicture.manager.upload;

import cn.hutool.core.io.FileUtil;
import com.jz.jzpicture.constant.PictureConstant;
import com.jz.jzpicture.exception.ErrorCode;
import com.jz.jzpicture.exception.ThrowUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 上传本地文件
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.manager.upload
 * @Project: jz-picture
 * @Date: 2025/2/12  14:05
 */
@Service
public class FilePictureUpload extends PictureUploadTemplate{
    @Override
    protected void validPicture(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        //1.校验是否为空
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
        //2.校验文件大小
        long size = multipartFile.getSize();
        ThrowUtils.throwIf(size > PictureConstant.MAX_SIZE, ErrorCode.PARAMS_ERROR, "文件大小不能超过2MB");
        //3.校验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final List<String> ALLOW_SUFFIX = Arrays.asList("jpg","jpeg","png","webp");
        ThrowUtils.throwIf(!ALLOW_SUFFIX.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件类型错误");
    }

    @Override
    protected String getOriginalFilename(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        return multipartFile.getOriginalFilename();
    }

    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        multipartFile.transferTo(file);
    }
}
