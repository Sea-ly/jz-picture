package com.jz.jzpicture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jz.jzpicture.model.dto.space.SpaceUserAddRequest;
import com.jz.jzpicture.model.dto.space.SpaceUserQueryRequest;
import com.jz.jzpicture.model.entity.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jz.jzpicture.model.vo.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 86151
* @description 针对表【space_user(空间用户关联)】的数据库操作Service
* @createDate 2025-02-22 18:10:11
*/
public interface SpaceUserService extends IService<SpaceUser> {

    /**
     * 添加空间成员
     * @param spaceUserAddRequest
     * @return
     */
    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    /**
     * 检验空间成员对象
     * @param spaceUser
     * @param add
     */
    void validSpaceUser(SpaceUser spaceUser, boolean add);

    /**
     * 封装查询条件
     * @param spaceUserQueryRequest
     * @return
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    /**
     * 获取空间成员封装类
     * @param spaceUser
     * @param request
     * @return
     */
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    /**
     * 查询封装类列表
     * @param spaceUserList
     * @return
     */
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);
}
