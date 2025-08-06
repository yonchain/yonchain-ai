package com.yonchain.ai.security.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 鉴权用户
 *
 * @author Cgy
 * @since 1.0.0
 */
public class YonchainUser extends User implements YonchainUserDetails {

    private final String userId;

    private final String email;

    private final String passwordSalt;

    public YonchainUser(String username, String password, String userId, String email,
                        String passwordSalt,
                        Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
        this.email = email;
        this.passwordSalt = passwordSalt;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }
}
