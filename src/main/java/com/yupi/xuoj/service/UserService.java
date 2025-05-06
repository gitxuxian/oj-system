package com.yupi.xuoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.xuoj.model.dto.user.UserQueryRequest;
import com.yupi.xuoj.model.entity.User;
import com.yupi.xuoj.model.vo.LoginUserVO;
import com.yupi.xuoj.model.vo.UserVO;

import java.util.List;
import javax.servlet.http.HttpServletRequest;



public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userName      用户名
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userName, String userPassword, String checkPassword, String userEmail, String phone, String code);

    /**
     * 用户登录
     *
     * @param phone
     * @param email
     * @param userPassword
     * @return
     */
    LoginUserVO userLogin(String phone, String email, String userPassword);


    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    User getLoginUser(Object loginID);
}
