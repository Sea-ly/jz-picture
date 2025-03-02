package com.jz.jzpicture.api.imagesearch.sub;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.jz.jzpicture.exception.BusinessException;
import com.jz.jzpicture.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:获取以图搜图页面地址（step 1）
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.api.imagesearch.sub
 * @Project: jz-picture
 * @Date: 2025/2/20  10:26
 */
@Slf4j
public class GetImagePageUrlApi {
    /**
     * 获取以图搜图页面地址
     *
     * @param imageUrl
     * @return
     */
    public static String getImagePageUrl(String imageUrl) {
        // image: https%3A%2F%2Fwww.codefather.cn%2Flogo.png
        //tn: pc
        //from: pc
        //image_source: PC_UPLOAD_URL
        //sdkParams:
        // 1. 准备请求参数
        Map<String, Object> formData = new HashMap<>();
        formData.put("image", imageUrl);
        formData.put("tn", "pc");
        formData.put("from", "pc");
        formData.put("image_source", "PC_UPLOAD_URL");
        // 获取当前时间戳
        long uptime = System.currentTimeMillis();
        // 请求地址
        String url = "https://graph.baidu.com/upload?uptime=" + uptime;
        String acsToken ="JTJqlM73be2Vyyy8O+BWiWCoNT4vC3K5HYzVzNehUIh0XGFWhr5y2oZYZdp/ogJdJgxNND5blLfIZ9o1mwd+lXXGVbk+RWAW1/CFR9llk71IN4Pwm365MFjiUBFg7eq3VPu2P9XBwhVwHFQRbgZ/+DtXFXakImIUALXhRT+BDWzaCy6DacCnXCrpqaEujMt2qgFxVRJNqowOD0911nAi2OkpGjbKn5VXyojq8YIzIzBoPtr0QHF1UD8u4E9+opf2fwniDTc1mAiAWGsAbLTm1g6WjQ7+5SqLInzEG5Vqw29jgoHKg7BjK8thZxX3pB7ddgnqIfY7gRkVXSj7ZBfRwwB37tcsPJZC4IVT6Zki2MoOr7DQ1Vw9bqr1NPeGIiY7tc/k+15IDM+XKj/CfTLBAsB7oiHjHxE8VzAoCnDlcIrM2qFtHmI4EWz80RmJfORw4Ut0M/d0xdsbp6mwFd98Y326/0Vj7u1oEzQG2SgZp0tahrlQFIzk98Rgf3blqkB2crTjRyJdD2jQXWPqFzXslQ==";
        try {
            // 2. 发送请求
            HttpResponse httpResponse = HttpRequest.post(url)
                    .form(formData)
                    .header("Acs-Token", acsToken)
                    .timeout(5000)
                    .execute();
            if (httpResponse.getStatus() != HttpStatus.HTTP_OK) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            // 解析响应
            // {"status":0,"msg":"Success","data":{"url":"https://graph.baidu.com/sc","sign":"1262fe97cd54acd88139901734784257"}}
            String body = httpResponse.body();
            Map<String, Object> result = JSONUtil.toBean(body, Map.class);
            // 3. 处理响应结果
            if (result == null || !Integer.valueOf(0).equals(result.get("status"))) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            // 对 URL 进行解码
            String rawUrl = (String) data.get("url");
            String searchResultUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8);
            // 如果 URL 为空
            if (StrUtil.isBlank(searchResultUrl)) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "未返回有效的结果地址");
            }
            return searchResultUrl;
        } catch (Exception e) {
            log.error("调用百度以图搜图接口失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜索失败");
        }
    }

    public static void main(String[] args) {
        // 测试以图搜图功能
        String imageUrl = "https://www.codefather.cn/logo.png";
        String searchResultUrl = getImagePageUrl(imageUrl);
        System.out.println("搜索成功，结果 URL：" + searchResultUrl);
    }
}

