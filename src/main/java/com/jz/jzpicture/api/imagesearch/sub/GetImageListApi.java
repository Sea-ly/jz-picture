package com.jz.jzpicture.api.imagesearch.sub;

/**
 * @Description:
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.api.imagesearch.sub
 * @Project: jz-picture
 * @Date: 2025/2/20  11:15
 */

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jz.jzpicture.api.imagesearch.model.ImageSearchResult;
import com.jz.jzpicture.exception.BusinessException;
import com.jz.jzpicture.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 获取图片列表（step 3）
 */
@Slf4j
public class GetImageListApi {

    /**
     * 获取图片列表
     *
     * @param url
     * @return
     */
    public static List<ImageSearchResult> getImageList(String url) {
        try {
            // 发起GET请求
            HttpResponse response = HttpUtil.createGet(url).execute();

            // 获取响应内容
            int statusCode = response.getStatus();
            String body = response.body();

            // 处理响应
            if (statusCode == 200) {
                // 解析 JSON 数据并处理
                return processResponse(body);
            } else {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
        } catch (Exception e) {
            log.error("获取图片列表失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取图片列表失败");
        }
    }

    /**
     * 处理接口响应内容
     *
     * @param responseBody 接口返回的JSON字符串
     */
    private static List<ImageSearchResult> processResponse(String responseBody) {
        // 解析响应对象
        JSONObject jsonObject = new JSONObject(responseBody);
        if (!jsonObject.containsKey("data")) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未获取到图片列表");
        }
        JSONObject data = jsonObject.getJSONObject("data");
        if (!data.containsKey("list")) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未获取到图片列表");
        }
        JSONArray list = data.getJSONArray("list");
        return JSONUtil.toList(list, ImageSearchResult.class);
    }

    public static void main(String[] args) {
        String url = "https://graph.baidu.com/ajax/pcsimi?carousel=503&entrance=GENERAL&extUiData%5BisLogoShow%5D=1&inspire=general_pc&limit=30&next=2&render_type=card&session_id=3916248921356409604&sign=121c2e97cd54acd88139901740021188&tk=9120b&tpl_from=pc";
        List<ImageSearchResult> imageList = getImageList(url);
        System.out.println("搜索成功" + imageList);
    }
}