package com.jz.jzpicture.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.jz.jzpicture.config.CosClientConfig;
import com.jz.jzpicture.exception.BusinessException;
import com.jz.jzpicture.exception.ErrorCode;
import com.jz.jzpicture.exception.ThrowUtils;
import com.jz.jzpicture.model.dto.File.UploadPictureResult;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.manager
 * @Project: jz-picture
 * @Date: 2025/2/11  9:17
 */
@Service
@Slf4j
public class FileManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;
    @Resource CosManager cosManager;

    public UploadPictureResult uploadPicture(MultipartFile multipartFile, String uploadFilePrefix){
        // 校验图片
        vaildPicture(multipartFile);
        // 获取文件名
        String uuid = RandomUtil.randomString(16);
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, suffix);
        String uploadPath = String.format("/%s/%s",uploadFilePrefix, uploadFileName);

        File file = null;
        try {
            //创建临时文件
            file = File.createTempFile(uploadPath,null);
            multipartFile.transferTo(file);
            //上传文件
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            //封装返回结果
            int height = imageInfo.getHeight();
            int width = imageInfo.getWidth();
            double picScale = NumberUtil.round(width *1.0/ height,2).doubleValue();
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            uploadPictureResult.setUrl(cosClientConfig.getHost()+"/"+uploadPath);
            uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
            uploadPictureResult.setPicFormat(suffix);
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setPicHeight(height);
            uploadPictureResult.setPicWidth(width);
            uploadPictureResult.setPicScale(picScale);
            return uploadPictureResult;
        } catch (Exception e) {
            log.error("图片上传到对象存储失败",e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"文件上传失败");
        } finally {
            this.deleteFile(file);
        }


    }

    /**
     * 校验图片方法
     * @param multipartFile
     */
    public void vaildPicture(MultipartFile multipartFile) {
        //1.校验是否为空
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
        //2.校验文件大小
        long size = multipartFile.getSize();
        final long ONE_M = 1024 *1024;
        ThrowUtils.throwIf(size > 2 * ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过2MB");
        //3.校验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final List<String> ALLOW_SUFFIX = Arrays.asList("jpg","jpeg","png","webp");
        ThrowUtils.throwIf(!ALLOW_SUFFIX.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件类型错误");

    }
    public void deleteFile(File file){
        if (file != null) {
            //删除临时文件
            boolean delete = file.delete();
            if (!delete) {
                log.error("file delete error, filePath = {}", file.getAbsoluteFile());
            }
        }
    }


}
