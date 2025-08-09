package com.yonchain.ai.security.user;

import com.yonchain.ai.api.sys.User;
import com.yonchain.ai.api.sys.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

/**
 * yonchain用户详情服务类，实现Spring Security的UserDetailsService接口
 * 用于根据用户名或邮箱加载用户详情信息
 * <p>
 * 主要功能：
 * 1. 支持通过用户名或邮箱查询用户信息
 * 2. 验证用户是否存在，不存在时抛出UsernameNotFoundException
 * 3. 将用户信息转换为Spring Security认证所需的UserDetails对象
 * <p>
 * 注意：
 * - 使用正则表达式验证输入是否为邮箱格式
 * - 依赖UserService进行实际的用户数据查询
 *
 * @author Cgy
 * @since 1.0.0
 */
public class YonchainUserDetailsService implements UserDetailsService {

    //邮箱正则表达式
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private final UserService userService;

    public YonchainUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(username)) {
            throw new UsernameNotFoundException(
                    String.format("用户名 (%s) 未找到", username));
        }

        // 判断是否为userId，如果是userId，就根据userId查询用户，否则根据用户名查询用户
        if (username.startsWith("{userId}")) {
            return loadUserById(username.substring("{userId}".length()));
        }

        User user = null;
        //判断是否邮箱，如果是邮箱，就根据邮箱查询用户，否则根据用户名查询用户
        boolean isValid = username.matches(EMAIL_REGEX);
        if (isValid) {
            user = userService.getUserByEmail(username);
        } else {
            user = userService.getUserByUserName(username);
        }

        if (user == null) {
           /* throw new UsernameNotFoundException(
                    String.format("user (%s) could not be found", username));*/
            return null;
        }
        return new YonchainUser(
                user.getName(),
                user.getPassword(),
                user.getId(),
                user.getEmail(),
                user.getPasswordSalt(),
                new ArrayList<>()
        );
    }


    public UserDetails loadUserById(String userId) throws UsernameNotFoundException {
        User user = userService.getUserById(userId);
        if (user == null) {
            return null;
        }
        return new YonchainUser(
                user.getName(),
                user.getPassword(),
                user.getId(),
                user.getEmail(),
                user.getPasswordSalt(),
                new ArrayList<>()
        );
    }
}
