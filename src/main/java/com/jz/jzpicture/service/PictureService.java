package com.jz.jzpicture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.jzpicture.model.dto.picture.PictureQueryRequest;
import com.jz.jzpicture.model.dto.picture.PictureUploadRequest;
import com.jz.jzpicture.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jz.jzpicture.model.entity.User;
import com.jz.jzpicture.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author 86151
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-02-11 16:44:59
*/
public interface PictureService extends IService<Picture> {

    /**
     * 上传文件
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);


    /**
     * 获取查询条件
     * @param pictureQueryRequest
     * @return
     */
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);


    /**
     * 获取单个图片封装
     * @param picture
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 分页获取图片封装
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 图片校验
     * @param picture
     */
    void validPicture(Picture picture);
}
