package com.jz.jzpicture.api.imagesearch;

import com.jz.jzpicture.api.imagesearch.model.ImageSearchResult;
import com.jz.jzpicture.api.imagesearch.sub.GetImageFirstUrlApi;
import com.jz.jzpicture.api.imagesearch.sub.GetImageListApi;
import com.jz.jzpicture.api.imagesearch.sub.GetImagePageUrlApi;

import java.util.List;

/**
 * @Description:
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.api.imagesearch
 * @Project: jz-picture
 * @Date: 2025/2/20  11:23
 */
public class ImageSearchFacade {
    /**
     * 搜索图片
     * @param imageUrl
     * @return
     */
    public static List<ImageSearchResult> imageSearch(String imageUrl) {
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        List<ImageSearchResult> imageList = GetImageListApi.getImageList(imageFirstUrl);
        return imageList;
    }
    public static void main(String[] args) {
        List<ImageSearchResult> imageList = imageSearch("https://jz-picture-1340777869.cos.ap-chengdu.myqcloud.com/public/1886400906459959297/2025-02-18_rovwjIoECGcUmU77.webp");
        System.out.println("结果列表" + imageList);
    }
}
