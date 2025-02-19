package com.jz.jzpicture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.jzpicture.model.dto.space.SpaceAddRequest;
import com.jz.jzpicture.model.dto.space.SpaceQueryRequest;
import com.jz.jzpicture.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jz.jzpicture.model.entity.User;
import com.jz.jzpicture.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 86151
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-02-19 14:04:59
*/
public interface SpaceService extends IService<Space> {

    /**
     * 获取查询条件
     * @param spaceQueryRequest
     * @return
     */
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);


    /**
     * 获取单个空间封装
     * @param space
     * @param request
     * @return
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 分页获取空间封装
     * @param spacePage
     * @param request
     * @return
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 空间校验
     * @param space
     */
    void validSpace(Space space, Boolean add);

    /**
     * 根据空间级别填充数据
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);

    /**
     * 创建空间
     * @param spaceAddRequest
     * @param loginUser
     * @return
     */
    Long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);
}
