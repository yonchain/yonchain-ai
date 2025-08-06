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
public class Dify4jUser extends User implements Dify4jUserDetails {

    private final String userId;

    private final String email;

    private final String passwordSalt;

    public Dify4jUser(String username, String password, String userId, String email,
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
