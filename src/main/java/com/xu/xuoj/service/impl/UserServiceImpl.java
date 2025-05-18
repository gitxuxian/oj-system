package com.xu.xuoj.service.impl;

import static com.xu.xuoj.constant.UserConstant.USER_LOGIN_STATE;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.xuoj.common.ErrorCode;
import com.xu.xuoj.constant.CommonConstant;
import com.xu.xuoj.constant.ReConstant;
import com.xu.xuoj.exception.BusinessException;
import com.xu.xuoj.mapper.UserMapper;
import com.xu.xuoj.model.dto.user.UserQueryRequest;
import com.xu.xuoj.model.entity.User;
import com.xu.xuoj.model.enums.UserRoleEnum;
import com.xu.xuoj.model.vo.LoginUserVO;
import com.xu.xuoj.model.vo.UserVO;
import com.xu.xuoj.service.UserService;
import com.xu.xuoj.utils.SqlUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;


@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "xuxian";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public long userRegister(String userName, String userPassword, String checkPassword, String userEmail, String phone, String code) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userName, userPassword, checkPassword, userEmail, phone, code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userName.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!Validator.isEmail(userEmail)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
        }
        if (!PhoneUtil.isPhone(phone)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机格式错误");
        }
        // 密码和校验密码相同
        if (!ReUtil.isMatch(ReConstant.PASSWORD_RE, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码需8-20位且包含大小写字母");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        String codeRedis = redisTemplate.opsForValue().get("email_code:" + userEmail);
        if (!codeRedis.equals(code) || codeRedis.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }
        synchronized (phone.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", phone);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserName(userName);
            user.setUserAccount(IdUtil.getSnowflake().nextIdStr());
            user.setUserPassword(encryptPassword);
            user.setEmail(userEmail);
            user.setPhone(phone);

            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String phone, String email, String userPassword) {
        if (StringUtils.isEmpty(phone) && StringUtils.isEmpty(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入手机号或者邮箱");
        }
        if (StringUtils.isEmpty(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码为空");
        }
        if (StringUtils.isNotEmpty(phone) && !PhoneUtil.isPhone(phone)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机格式错误");
        }
        if (StringUtils.isNotEmpty(email) && !Validator.isEmail(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        if (!ReUtil.isMatch(ReConstant.PASSWORD_RE, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码格式错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        return this.getLoginUserVO(user);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
            sortField);
        return queryWrapper;
    }

    @Override
    public User getLoginUser(Object loginId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", loginId);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user query failed");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        //状态的存储
        LoginUserVO loginUserVO = this.getLoginUserVO(user);
        Map<String, String> userMap = new HashMap<>();
        userMap.put("username", loginUserVO.getUserName());
        userMap.put("email", loginUserVO.getEmail());
        userMap.put("avatar", loginUserVO.getUserAvatar());
        userMap.put("profile", loginUserVO.getUserProfile());
        String token = UUID.randomUUID().toString();
        String tokenKey = "user::info" + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        stringRedisTemplate.expire(tokenKey, 7, TimeUnit.DAYS);
        return user;
    }
}
