package com.atiguigu.yygh.user.service;

import com.atiguigu.yygh.model.user.UserInfo;
import com.atiguigu.yygh.vo.user.LoginVo;
import com.atiguigu.yygh.vo.user.UserAuthVo;
import com.atiguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {

    //用户手机号登录接口
    Map<String, Object> loginUser(LoginVo loginVo);

    //根据openid判断
    UserInfo selectWxInfoOpenId(String openid);

    //用户认证接口
    void userAuth(Long userId, UserAuthVo userAuthVo);

    //用户列表（条件查询带分页）
    IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo);

    //根据用户id锁定用户
    void lock(Long userId, Integer status);

    //用户详情
    Map<String, Object> show(Long userId);

    /**
     * 认证审批
     * @param userId
     * @param authStatus 2：通过 -1：不通过
     */
    void approval(Long userId, Integer authStatus);

    UserInfo getUserInfoById(Long userId);
}
