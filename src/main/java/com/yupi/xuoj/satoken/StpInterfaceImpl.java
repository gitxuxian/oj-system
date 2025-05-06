package com.yupi.xuoj.satoken;

import cn.dev33.satoken.model.wrapperInfo.SaDisableWrapperInfo;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.yupi.xuoj.model.entity.User;
import com.yupi.xuoj.service.UserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限加载接口实现类
 */

// 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
@Component
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private UserService userService;

    @Override
    public List<String> getPermissionList(Object loginId, String s) {
        return null;
    }

    @Override
    public List<String> getRoleList(Object loginId, String s) {
        List<String> list = new ArrayList<>();
        User user = userService.getById(Long.parseLong(loginId.toString()));
        String userRole = user.getUserRole();
        list.add(userRole);
        return list;
    }

}
