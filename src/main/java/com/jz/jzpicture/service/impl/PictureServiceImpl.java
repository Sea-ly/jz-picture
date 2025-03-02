package com.jz.jzpicture.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jz.jzpicture.api.aliyunAI.model.CreateOutPaintingTaskRequest;
import com.jz.jzpicture.api.aliyunAI.model.CreateOutPaintingTaskResponse;
import com.jz.jzpicture.api.aliyunAI.sub.AliYunAiApi;
import com.jz.jzpicture.common.ResultUtils;
import com.jz.jzpicture.config.CosClientConfig;
import com.jz.jzpicture.exception.BusinessException;
import com.jz.jzpicture.exception.ErrorCode;
import com.jz.jzpicture.exception.ThrowUtils;
import com.jz.jzpicture.manager.CosManager;
import com.jz.jzpicture.manager.mq.RabbitMQConfig;
import com.jz.jzpicture.manager.mq.SpaceQuotaUpdateMessage;
import com.jz.jzpicture.manager.upload.FilePictureUpload;
import com.jz.jzpicture.manager.upload.PictureUploadTemplate;
import com.jz.jzpicture.manager.upload.UrlPictureUpload;
import com.jz.jzpicture.model.dto.file.UploadPictureResult;
import com.jz.jzpicture.model.dto.picture.*;
import com.jz.jzpicture.model.entity.Picture;
import com.jz.jzpicture.model.entity.Space;
import com.jz.jzpicture.model.entity.User;
import com.jz.jzpicture.model.enums.PictureReviewStatusEnum;
import com.jz.jzpicture.model.vo.PictureVO;
import com.jz.jzpicture.model.vo.UserVO;
import com.jz.jzpicture.service.PictureService;
import com.jz.jzpicture.mapper.PictureMapper;
import com.jz.jzpicture.service.SpaceService;
import com.jz.jzpicture.service.UserService;
import com.jz.jzpicture.utils.ColorSimilarUtils;
import com.jz.jzpicture.utils.ColorTransformUtils;
import com.qcloud.cos.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 86151
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-02-11 16:44:59
*/
@Service
@Slf4j
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{
    @Resource
    private UserService userService;
    @Resource
    private FilePictureUpload filePictureUpload;
    @Resource
    private UrlPictureUpload urlPictureUpload;
    @Resource
    private CosManager cosManager;
    @Resource
    private SpaceService spaceService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private AliYunAiApi aliYunAiApi;
    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RabbitMQConfig rabbitMQConfig;


    /**
     * 上传图片
     * @param inputSource
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    @Override
    public PictureVO uploadPicture(Object inputSource , PictureUploadRequest pictureUploadRequest, User loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        Long loginUserId = loginUser.getId();
        Long spaceId = pictureUploadRequest.getSpaceId();
        if(spaceId != null){
            //判断是否存在空间
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
//            //如果空间存在，则需要判断是否是空间管理者
//            ThrowUtils.throwIf(!loginUserId.equals(space.getUserId()), ErrorCode.NO_AUTH_ERROR, "没有空间权限");
            //校验额度
            if(space.getTotalSize() >= space.getMaxSize()){
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间大小不足");
            }
            if(space.getTotalCount() >= space.getMaxCount()){
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间条数不足");
            }
        }
        //标志是否是更新
        Boolean isUpdate = false;
        //用于判断是新增还是更新图片
        Long pictureId = null;
        if(pictureUploadRequest != null){
             pictureId = pictureUploadRequest.getId();
        }
        //如果是更新图片，需要校验图片是否存在
        Picture oldPicture = new Picture();
        if( pictureId != null){
            oldPicture = this.getById(pictureId);
            isUpdate = true;
            ThrowUtils.throwIf(oldPicture ==null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
//            //仅管理员或本人可编辑
//            if(!oldPicture.getUserId().equals(loginUserId) && !userService.isAdmin(loginUser)){
//                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//            }
            // 校验空间是否一致
            // 没传 spaceId，则复用原有图片的 spaceId（这样也兼容了公共图库）
            if (spaceId == null) {
                if (oldPicture.getSpaceId() != null) {
                    spaceId = oldPicture.getSpaceId();
                }
            } else {
                // 传了 spaceId，必须和原图片的空间 id 一致
                if (ObjUtil.notEqual(spaceId, oldPicture.getSpaceId())) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间 id 不一致");
                }
            }
        }

        //上传图片，得到信息
        // 按照用户 id 划分目录 => 按照空间划分目录
        String uploadPathPrefix;
        if (spaceId == null) {
            // 公共图库
            uploadPathPrefix = String.format("public/%s", loginUser.getId());
        } else {
            // 空间
            uploadPathPrefix = String.format("space/%s", spaceId);
        }
        //根据inputSource类型决定上传方式
        PictureUploadTemplate uploadTemplate = filePictureUpload;
        if(inputSource instanceof String){
            uploadTemplate = urlPictureUpload;
        }
        UploadPictureResult uploadPictureResult = uploadTemplate.uploadPicture(inputSource, uploadPathPrefix);
        //构造要入库的图片信息
        Picture picture = new Picture();
        picture.setSpaceId(spaceId); // 指定空间 id
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setThumbnailUrl(uploadPictureResult.getThumbnailUrl());
        String picName = uploadPictureResult.getPicName();
        if(pictureUploadRequest != null && StrUtil.isNotBlank(pictureUploadRequest.getPicName())){
            picName = pictureUploadRequest.getPicName();
        }
        picture.setName(picName);
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicColor(ColorTransformUtils.getStandardColor(uploadPictureResult.getPicColor()));
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUserId);
        //补充审核参数
        this.fillReviewParams(picture, loginUser);
        //如果pictureId不为空，则表示为更新，否则是新增
        if(isUpdate){
            //为更新，需要补充id和编辑时间
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        Long finalSpaceId = spaceId;
        Boolean isUpdate1 = isUpdate;
        Picture finalOldPicture = oldPicture;
        //开启事务
//        transactionTemplate.execute(status -> {
//            boolean result = this.saveOrUpdate(picture);
//            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败");
//            if(finalSpaceId != null){
//                //空间id不为空（即上传到个人空间才更新额度）
//                if(isUpdate1){
//                    //为更新操作，则需删除原来数据库存储的图片对象
//                    this.clearPictureFile(finalOldPicture);
//                    //更新额度
//                    boolean result1 = spaceService.lambdaUpdate()
//                            .eq(Space::getId, finalSpaceId)
//                            .setSql("totalSize = totalSize + " + (picture.getPicSize() - finalOldPicture.getPicSize()))
//                            .update();
//                    ThrowUtils.throwIf( !result1, ErrorCode.OPERATION_ERROR, "额度更新失败");
//                }else {
//                    //新增操作，更新额度
//                    boolean result2 = spaceService.lambdaUpdate()
//                            .eq(Space::getId, finalSpaceId)
//                            .setSql("totalSize = totalSize + " + picture.getPicSize())
//                            .setSql("totalCount = totalCount + 1")
//                            .update();
//                    ThrowUtils.throwIf( !result2, ErrorCode.OPERATION_ERROR, "额度更新失败");
//                }
//            }
//
//            return picture;
//        });
        transactionTemplate.execute(status -> {
            // 保存或更新图片
            boolean saveResult = this.saveOrUpdate(picture);
            ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "图片上传失败");

            // 构造MQ消息
            if (finalSpaceId != null) {
                SpaceQuotaUpdateMessage message = new SpaceQuotaUpdateMessage();
                message.setSpaceId(finalSpaceId);
                message.setOperationId(UUID.randomUUID().toString());

                if (isUpdate1) {
                    // 更新操作：计算新旧图片大小差
                    message.setSizeDelta(picture.getPicSize() - finalOldPicture.getPicSize());
                    message.setCountDelta(0);
                } else {
                    // 新增操作
                    message.setSizeDelta(picture.getPicSize());
                    message.setCountDelta(1);
                }

                // 事务提交后发送消息
                TransactionSynchronizationManager.registerSynchronization(
                        new TransactionSynchronization() {
                            @Override
                            public void afterCommit() {
                                rabbitTemplate.convertAndSend(
                                        RabbitMQConfig.SPACE_UPDATE_EXCHANGE,
                                        RabbitMQConfig.SPACE_UPDATE_ROUTING_KEY,
                                        message
                                );
                            }
                        }
                );
            }
            return null;
        });
        PictureVO pictureVO = PictureVO.objToVo(picture);
        return pictureVO;
    }


    /**
     * 获取查询条件
     * @param pictureQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        Long reviewerId = pictureQueryRequest.getReviewerId();
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        Long spaceId = pictureQueryRequest.getSpaceId();
        boolean nullSpaceId = pictureQueryRequest.isNullSpaceId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        Date startEditTime = pictureQueryRequest.getStartEditTime();
        Date endEditTime = pictureQueryRequest.getEndEditTime();
        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("name", searchText)
                    .or()
                    .like("introduction", searchText)
            );
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId);
        queryWrapper.isNull(nullSpaceId, "spaceId");
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.ge(ObjUtil.isNotEmpty(startEditTime), "editTime", startEditTime);
        queryWrapper.lt(ObjUtil.isNotEmpty(endEditTime), "editTime", endEditTime);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 获取单个图片封装
     * @param picture
     * @param request
     * @return
     */
    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        // 对象转封装类
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 关联查询用户信息
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            pictureVO.setUserVO(userVO);
        }
        return pictureVO;
    }
    /**
     * 分页获取图片封装
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::objToVo).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUserVO(userService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }

    /**
     * 图片校验
     * @param picture
     */
    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        // 修改数据时，id 不能为空，有参数则校验
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR, "id 不能为空");
        if (StrUtil.isNotBlank(url)) {
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url 过长");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 800, ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }

    /**
     * 图片审核
     * @param pictureReviewRequest
     * @param loginUser
     */
    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        //参数校验
        Long id = pictureReviewRequest.getId();
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        if(id == null || reviewStatusEnum == null ||reviewStatusEnum == PictureReviewStatusEnum.REVIEWING){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //图片校验
        // 如果不存在该图片则抛出异常
        Picture picture = this.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        //修改后的状态和原状态一致则抛出异常
        ThrowUtils.throwIf(picture.getReviewStatus().equals(reviewStatus), ErrorCode.NOT_FOUND_ERROR,"请勿重复审核");
        //更新审核状态
        Picture updatePicture = new Picture();
        BeanUtil.copyProperties(pictureReviewRequest, updatePicture);
        updatePicture.setReviewerId(loginUser.getId());
        updatePicture.setReviewTime(new Date());
        boolean b = this.updateById(updatePicture);
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);

    }

    /**
     *补充审核参数
     * @param picture
     * @param loginUser
     */
    @Override
    public void fillReviewParams(Picture picture, User loginUser){
        if(userService.isAdmin(loginUser)){
            //管理员自动过审核
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewerId(loginUser.getId());
            picture.setReviewTime(new Date());
            picture.setReviewMessage("管理员自动过审");
        }else{
            //非管理员，创建或者编辑都要将状态设置为审核中
        }
    }

    /**
     * 批量抓取和创建图片
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return
     */
    @Override
    public Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
        //校验参数
        Integer count = pictureUploadByBatchRequest.getCount();
        ThrowUtils.throwIf(count > 30, ErrorCode.PARAMS_ERROR,"最多批量上传30条！");
        String searchText = pictureUploadByBatchRequest.getSearchText();
        String namePrefix = pictureUploadByBatchRequest.getNamePrefix();
        //文件前缀为空则默认为搜索关键字
        if(StrUtil.isBlank(namePrefix)){
            namePrefix = searchText;
        }
        //抓取内容
        String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", searchText);
        Document document;
        try {
            document = Jsoup.connect(fetchUrl).get();
        } catch (IOException e) {
            log.error("获取页面失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取页面失败");
        }
        Element div = document.getElementsByClass("dgControl").first();
        if (ObjUtil.isEmpty(div)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取元素失败");
        }
        Elements imgElementList = div.select("img.mimg");
        int uploadCount = 0;
        // 遍历元素，依次处理上传图片
        for (Element imgElement : imgElementList) {
            String fileUrl = imgElement.attr("src");
            if (StrUtil.isBlank(fileUrl)) {
                log.info("当前链接为空，已跳过：{}", fileUrl);
                continue;
            }
            // 处理图片的地址，防止转义或者和对象存储冲突的问题
            int questionMarkIndex = fileUrl.indexOf("?");
            if (questionMarkIndex > -1) {
                fileUrl = fileUrl.substring(0, questionMarkIndex);
            }
            // 上传图片
            PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
            pictureUploadRequest.setFileUrl(fileUrl);
            pictureUploadRequest.setPicName(namePrefix + (uploadCount + 1));
            try {
                PictureVO pictureVO = this.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
                log.info("图片上传成功，id = {}", pictureVO.getId());
                uploadCount++;
            } catch (Exception e) {
                log.error("图片上传失败", e);
                continue;
            }
            if (uploadCount >= count) {
                break;
            }
        }
        return uploadCount;
    }

    /**
     * 删除图片
     * @param pictureId
     * @param loginUser
     */
    @Override
    public void deletePicture(long pictureId, User loginUser) {
        ThrowUtils.throwIf(pictureId <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        // 判断是否存在
        Picture oldPicture = this.getById(pictureId);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 校验权限
        //checkPictureAuth(loginUser, oldPicture);
//       // 开启事务
//        transactionTemplate.execute(status -> {
//            // 操作数据库
//            boolean result = this.removeById(pictureId);
//            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
//            // 释放额度
//            Long spaceId = oldPicture.getSpaceId();
//            if (spaceId != null) {
//                boolean update = spaceService.lambdaUpdate()
//                        .eq(Space::getId, spaceId)
//                        .setSql("totalSize = totalSize - " + oldPicture.getPicSize())
//                        .setSql("totalCount = totalCount - 1")
//                        .update();
//                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
//            }
//            return true;
//        });
        transactionTemplate.execute(status -> {
            // 操作数据库
            boolean result = this.removeById(pictureId);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

            // 构造MQ消息
            Long spaceId = oldPicture.getSpaceId();
            if (spaceId != null) {
                SpaceQuotaUpdateMessage message = new SpaceQuotaUpdateMessage();
                message.setSpaceId(spaceId);
                message.setOperationId(UUID.randomUUID().toString());
                    // 更新操作：计算新旧图片大小差
                    message.setSizeDelta( - oldPicture.getPicSize());
                    message.setCountDelta(-1);
                // 事务提交后发送消息
                TransactionSynchronizationManager.registerSynchronization(
                        new TransactionSynchronization() {
                            @Override
                            public void afterCommit() {
                                rabbitTemplate.convertAndSend(
                                        RabbitMQConfig.SPACE_UPDATE_EXCHANGE,
                                        RabbitMQConfig.SPACE_UPDATE_ROUTING_KEY,
                                        message
                                );
                            }
                        }
                );
            }
            return null;
        });
        // 异步清理文件
        this.clearPictureFile(oldPicture);
    }

    /**
     * 删除对象存储中文件
     * @param oldPicture
     */
    @Override
    @Async
    public void clearPictureFile(Picture oldPicture) {
        //查询数据库中是否还有使用此地址的图片
        String url = oldPicture.getUrl();
        Long count = this.lambdaQuery()
                .eq(Picture::getUrl, url)
                .count();
        // 如果没有其他记录使用这个URL，则删除文件
        if(count == 0) {
            try {
                cosManager.deleteObject(oldPicture.getUrl());
                // 处理缩略图
                String thumbnailUrl = oldPicture.getThumbnailUrl();
                if(StrUtil.isNotBlank(thumbnailUrl)){
                    cosManager.deleteObject(oldPicture.getThumbnailUrl());
                }
            } catch (Exception e) {
                log.error("删除文件失败: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 编辑图片
     * @param pictureEditRequest
     * @param loginUser
     */
    @Override
    public void editPicture(PictureEditRequest pictureEditRequest, User loginUser) {
        // 在此处将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        // 设置编辑时间
        picture.setEditTime(new Date());
        // 数据校验
        this.validPicture(picture);
        // 判断是否存在
        long id = pictureEditRequest.getId();
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 校验权限
        //checkPictureAuth(loginUser, oldPicture);
        // 补充审核参数
        this.fillReviewParams(picture, loginUser);
        // 操作数据库
        boolean result = this.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }


    /**
     * 校验权限
     * @param loginUser
     * @param picture
     */
    @Override
    public void checkPictureAuth(User loginUser, Picture picture) {
        Long spaceId = picture.getSpaceId();
        if (spaceId == null) {
            // 公共图库，仅本人或管理员可操作
            if (!picture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        } else {
            // 私有空间，仅空间管理员可操作
            if (!picture.getUserId().equals(loginUser.getId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
    }

    /**
     * 颜色搜图
     * @param picColor
     * @param spaceId
     * @param loginUser
     * @return
     */
    @Override
    public List<PictureVO> searchPictureByColor(String picColor, Long spaceId, User loginUser) {
        // 1.参数校验
        ThrowUtils.throwIf(picColor == null || spaceId == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        // 2.校验空间权限
        Space space = spaceService.getById(spaceId);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        ThrowUtils.throwIf(space.getUserId().equals(loginUser.getId()), ErrorCode.NOT_FOUND_ERROR, "没有空间访问权限");
        // 3.查询该空间下有主色调的所有图片
        List<Picture> pictureList = this.lambdaQuery()
                .eq(Picture::getSpaceId, spaceId)
                .isNotNull(Picture::getPicColor)
                .list();
        //如果没有图片直接返回空列表
        if(CollUtil.isEmpty(pictureList)){
            return Collections.emptyList();
        }
        // 4.按相似度排序
        //将目标颜色转化为Color对象
        Color targetColor = Color.decode(picColor);
        //利用stream流快速排序(默认以返回值从小到大, 要想从大到小返回负值即可)
        List<Picture> sortedPictureList = pictureList.stream().sorted(Comparator.comparingDouble(picture -> {
            //提取图片主色调
            String hexColor = picture.getPicColor();
            //没有主色调的放到最后
            if (hexColor == null) {
                return Double.MAX_VALUE;
            }
            Color pictureColor = Color.decode(hexColor);
            //相似度越大越靠前，所以得取负值
            return -ColorSimilarUtils.calculateSimilarity(targetColor, pictureColor);
        })).limit(12).collect(Collectors.toList());

        return sortedPictureList.stream()
                .map(PictureVO::objToVo)
                .collect(Collectors.toList());
    }

    /**
     * 批量编辑图片
     * @param pictureEditByBatchRequest
     * @param loginUser
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser) {
        List<Long> pictureIdList = pictureEditByBatchRequest.getPictureIdList();
        String category = pictureEditByBatchRequest.getCategory();
        Long spaceId = pictureEditByBatchRequest.getSpaceId();
        List<String> tags = pictureEditByBatchRequest.getTags();
        String nameRule = pictureEditByBatchRequest.getNameRule();
        //参数校验
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(CollUtil.isEmpty(pictureIdList), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(spaceId != null, ErrorCode.PARAMS_ERROR);
        Space space = spaceService.getById(spaceId);
        ThrowUtils.throwIf(space == null , ErrorCode.PARAMS_ERROR, "空间不存在");
        ThrowUtils.throwIf(!space.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "没有空间访问权限");
        //查询指定图片，选取需要的字段
        List<Picture> pictureList = this.lambdaQuery()
                .select(Picture::getId, Picture::getSpaceId)
                .in(Picture::getId, pictureIdList)
                .eq(Picture::getSpaceId, spaceId)
                .list();
        if(CollUtil.isEmpty(pictureList)){
            return;
        }
        pictureList.forEach(picture -> {
            //更新分类和标签
            if(category != null){
                picture.setCategory(category);
            }
            if(tags != null){
                picture.setTags(JSONUtil.toJsonStr(tags));
            }

        });
        // 批量重命名
        fillPictureWithNameRule(pictureList, nameRule);
        //批量更新
        boolean result = this.updateBatchById(pictureList);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);



    }

    /**
     * 创建AI扩图请求类
     * @param createPictureOutPaintingTaskRequest
     * @param loginUser
     * @return
     */
    @Override
    public CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser) {
        // 获取图片信息
        Long pictureId = createPictureOutPaintingTaskRequest.getPictureId();
        Picture picture = Optional.ofNullable(this.getById(pictureId))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR));
        // 权限校验
        //checkPictureAuth(loginUser, picture);
        // 构造请求参数
        CreateOutPaintingTaskRequest taskRequest = new CreateOutPaintingTaskRequest();
        CreateOutPaintingTaskRequest.Input input = new CreateOutPaintingTaskRequest.Input();
        input.setImageUrl(picture.getUrl());
        taskRequest.setInput(input);
        BeanUtil.copyProperties(createPictureOutPaintingTaskRequest, taskRequest);
        // 创建任务
        return aliYunAiApi.createOutPaintingTask(taskRequest);
    }


    /**
     * 批量填充重命名,格式: 图片{序号}
     * @param pictureList
     * @param nameRule
     */
    private void fillPictureWithNameRule(List<Picture> pictureList, String nameRule) {
        if(StrUtil.isBlank(nameRule) || CollUtil.isEmpty(pictureList)){
            return;
        }
        long count = 1;
        try {
            for (Picture picture : pictureList) {
                picture.setName(nameRule.replaceAll("\\{序号}", String.valueOf(count++)));
            }

        }catch (Exception e){
            log.error("名称解析错误", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "名称解析错误");
        }
    }

    /**
     * 用户上传头像
     * @param multipartFile
     * @param loginUser
     * @return
     */
    @Override
    public String uploadAvatar(MultipartFile multipartFile, User loginUser) {
        // 自己拼接文件上传路径，而不是使用原始文件名称，可以增强安全性
        String fileSuffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
        String uuid = RandomUtil.randomString(6);
        String uploadFilename = String.format("%s_%s_%s%s", DateUtil.formatDate(new Date()),uuid, loginUser.getId(), fileSuffix);
        String uploadPath = String.format("/%s/%s", "avatar", uploadFilename);
        File file = null;
        try {
            // 3. 创建临时文件，获取文件到服务器
            file = File.createTempFile(uploadPath, null);
            // 处理文件
            multipartFile.transferTo(file);
            // 4. 上传图片到对象存储
            PutObjectResult putObjectResult = cosManager.putObject(uploadPath, file);
            return cosClientConfig.getHost() + "/" + uploadPath;
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        } finally {
            if (file != null) {
                //删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filePath = {}", file.getAbsoluteFile());
                }
            }
        }
    }


}




