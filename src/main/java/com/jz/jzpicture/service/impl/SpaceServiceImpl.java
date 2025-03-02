package com.jz.jzpicture.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.jzpicture.exception.BusinessException;
import com.jz.jzpicture.exception.ErrorCode;
import com.jz.jzpicture.exception.ThrowUtils;
import com.jz.jzpicture.model.dto.space.SpaceAddRequest;
import com.jz.jzpicture.model.dto.space.SpaceQueryRequest;
import com.jz.jzpicture.model.entity.Space;
import com.jz.jzpicture.model.entity.SpaceUser;
import com.jz.jzpicture.model.entity.User;
import com.jz.jzpicture.model.enums.SpaceLevelEnum;
import com.jz.jzpicture.model.enums.SpaceRoleEnum;
import com.jz.jzpicture.model.enums.SpaceTypeEnum;
import com.jz.jzpicture.model.vo.SpaceVO;
import com.jz.jzpicture.model.vo.UserVO;
import com.jz.jzpicture.service.SpaceService;
import com.jz.jzpicture.mapper.SpaceMapper;
import com.jz.jzpicture.service.SpaceUserService;
import com.jz.jzpicture.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 86151
* @description 针对表【space(空间)】的数据库操作Service实现
* @createDate 2025-02-19 14:04:59
*/
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space>
    implements SpaceService{
    @Resource
    private UserService userService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private SpaceUserService spaceUserService;

    /**
     * 创建空间
     * @param spaceAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public Long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
        //默认值
        if(StrUtil.isBlank(spaceAddRequest.getSpaceName())){
            spaceAddRequest.setSpaceName("默认空间");
        }
        if(spaceAddRequest.getSpaceLevel() == null){
            spaceAddRequest.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
        }
        if (spaceAddRequest.getSpaceType() == null) {
            spaceAddRequest.setSpaceType(SpaceTypeEnum.PRIVATE.getValue());
        }
        Space space = new Space();
        BeanUtil.copyProperties(spaceAddRequest, space);
        //校验数据
        this.validSpace(space, true);
        //填充数据
        this.fillSpaceBySpaceLevel(space);
        //权限校验(非管理员和非vip不能创建高级别空间)
        if(spaceAddRequest.getSpaceLevel() != SpaceLevelEnum.COMMON.getValue() && !userService.isAdmin(loginUser) && !userService.isVip(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无法创建指定级别的空间");
        }
        Long userId = loginUser.getId();
        space.setUserId(userId);
        //针对用户进行加锁
        String lock = String.valueOf(userId).intern();
        synchronized(lock){
            Long newSpaceId = transactionTemplate.execute(status->{
                if(!userService.isAdmin(loginUser)){
                    boolean exists = this.lambdaQuery()
                            .eq(Space::getUserId, userId)
                            .eq(Space::getSpaceType, spaceAddRequest.getSpaceType())
                            .exists();
                    ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR,"每个用户每类空间仅能创建一个");
                }
                //写入数据
                Boolean result = this.save(space);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
                // 如果是团队空间，关联新增团队成员记录
                if(spaceAddRequest.getSpaceType()==SpaceTypeEnum.TEAM.getValue()){
                    SpaceUser spaceUser = new SpaceUser();
                    spaceUser.setSpaceId(space.getId());
                    spaceUser.setUserId(userId);
                    spaceUser.setSpaceRole(SpaceRoleEnum.ADMIN.getValue());
                    boolean result1 = spaceUserService.save(spaceUser);
                    ThrowUtils.throwIf(!result1, ErrorCode.OPERATION_ERROR, "创建团队空间记录失败");
                }
                return space.getId();
            });
            return  newSpaceId;
        }
    }

    @Override
    public void validSpace(Space space, Boolean add) {
        Integer spaceLevel = space.getSpaceLevel();
        String spaceName = space.getSpaceName();
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(spaceLevel);
        Integer spaceType = space.getSpaceType();
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(spaceType);
        //创建空间时
        if(add){
            ThrowUtils.throwIf(spaceName == null, ErrorCode.PARAMS_ERROR, "空间名不能为空");
            ThrowUtils.throwIf(spaceLevel == null, ErrorCode.PARAMS_ERROR, "空间级别不能为空");
            ThrowUtils.throwIf(spaceType == null, ErrorCode.PARAMS_ERROR, "空间类型不能为空");
        }
        if(spaceLevel != null && spaceLevelEnum == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "空间级别不存在");
        }
        if(spaceType != null && spaceTypeEnum == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "空间类型不存在");
        }
        if(spaceName != null && spaceName.length() > 30){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "空间名称过长");
        }

    }



    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        // 对象转封装类
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        // 关联查询用户信息
        Long userId = space.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            spaceVO.setUser(userVO);
        }
        return spaceVO;
    }

    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {
        List<Space> spaceList = spacePage.getRecords();
        Page<SpaceVO> spaceVOPage = new Page<>(spacePage.getCurrent(), spacePage.getSize(), spacePage.getTotal());
        if (CollUtil.isEmpty(spaceList)) {
            return spaceVOPage;
        }
        // 对象列表 => 封装对象列表
        List<SpaceVO> spaceVOList = spaceList.stream()
                .map(SpaceVO::objToVo)
                .collect(Collectors.toList());
        // 1. 关联查询用户信息
        // 1,2,3,4
        Set<Long> userIdSet = spaceList.stream().map(Space::getUserId).collect(Collectors.toSet());
        // 1 => user1, 2 => user2
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        spaceVOList.forEach(spaceVO -> {
            Long userId = spaceVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceVO.setUser(userService.getUserVO(user));
        });
        spaceVOPage.setRecords(spaceVOList);
        return spaceVOPage;
    }
    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        if (spaceQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceQueryRequest.getId();
        Long userId = spaceQueryRequest.getUserId();
        String spaceName = spaceQueryRequest.getSpaceName();
        Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
        String sortField = spaceQueryRequest.getSortField();
        String sortOrder = spaceQueryRequest.getSortOrder();
        Integer spaceType = spaceQueryRequest.getSpaceType();
        // 拼接查询条件
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(spaceName), "spaceName", spaceName);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceLevel), "spaceLevel", spaceLevel);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceType), "spaceType", spaceType);
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 根据空间级别填充信息
     * @param space
     */
    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        Integer spaceLevel = space.getSpaceLevel();
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(spaceLevel);
        if(spaceLevelEnum != null){
            if(space.getMaxCount() == null){
                space.setMaxCount(spaceLevelEnum.getMaxCount());
            }
            if(space.getTotalSize() == null){
                space.setMaxSize(spaceLevelEnum.getMaxSize());
            }
        }
    }

    /**
     * 空间权限校验
     *
     * @param loginUser
     * @param space
     */
    @Override
    public void checkSpaceAuth(User loginUser, Space space) {
        // 仅本人或管理员可访问
        if (!space.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
    }

    @Override
    public boolean updateQuotaWithLock(Long spaceId, Long sizeDelta, Integer countDelta) {
        if (sizeDelta >= 0) {
            // 如果是增加容量，直接更新
            return this.update(new LambdaUpdateWrapper<Space>()
                    .eq(Space::getId, spaceId)
                    .setSql("totalSize = totalSize + " + sizeDelta)
                    .setSql("totalCount = totalCount + " + countDelta)
            );
        } else {
            // 如果是减少容量，使用GREATEST函数确保结果不小于0
            return this.update(new LambdaUpdateWrapper<Space>()
                    .eq(Space::getId, spaceId)
                    .setSql("totalSize = GREATEST(0, totalSize + " + sizeDelta + ")")
                    .setSql("totalCount = GREATEST(0, totalCount + " + countDelta + ")")
            );
        }
    }



}




