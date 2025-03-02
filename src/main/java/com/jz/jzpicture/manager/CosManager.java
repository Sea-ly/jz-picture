package com.jz.jzpicture.manager;

import cn.hutool.core.io.FileUtil;
import com.jz.jzpicture.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.manager
 * @Project: jz-picture
 * @Date: 2025/2/11  9:17
 */
@Component
@Slf4j
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;


    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 下载对象
     *
     * @param key 唯一键
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

    /**
     * 删除对象(根据url)
     * @param url
     */
    public void deleteObject(String url) throws CosClientException {
        //删掉url的域名得到key
        String key = url.replace(cosClientConfig.getHost(), "")
                .replaceFirst("^/", ""); // 移除开头的斜杠（如果有）
        cosClient.deleteObject(cosClientConfig.getBucket(), key);
    }




    /**
     * 上传对象,附带图片信息
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        // 对图片进行处理(获取基本信息也被视作一种图片的处理)
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);
        List<PicOperations.Rule> rules = new ArrayList<>();
        // 图片压缩(jpg格式)
        String webKey = FileUtil.mainName(key) + ".jpg";
        PicOperations.Rule compressRule = new PicOperations.Rule();
        compressRule.setBucket(cosClientConfig.getBucket());
        compressRule.setFileId(webKey);
        // 通用转换格式参数
        String commonParams = "/strip/format/jpg";
        if (file.length() > 3000 * 1024) {
            // 大图压缩：
            compressRule.setRule("imageMogr2" + commonParams + "/quality/85");
        } else if (file.length() > 500 * 1024) {
            // 中等压缩：
            compressRule.setRule("imageMogr2" + commonParams + "/quality/95");
        } else {
            // 小图：保持质量
            compressRule.setRule("imageMogr2" + commonParams );
        }
        rules.add(compressRule);

        //缩略图处理
        //仅对大于100KB的图片进行处理
        if(file.length() > 100 * 1024){
            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
            String thumbnailRuleKey = FileUtil.mainName(key) + "_thumbnail."+ FileUtil.getSuffix(key);
            thumbnailRule.setBucket(cosClientConfig.getBucket());
            thumbnailRule.setFileId(thumbnailRuleKey);
            //缩放规则
            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>",512,512));
            rules.add(thumbnailRule);
        }
        //构造处理参数
        picOperations.setRules(rules);
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }




}
