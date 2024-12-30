package com.shuting.springsecurityoauth2.commons.config.securityConfig;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shuting.springsecurityoauth2.commons.exception.MissingPropertyException;
import com.shuting.springsecurityoauth2.commons.exception.ResourceAlreadyExistException;
import com.shuting.springsecurityoauth2.entity.user.OauthUser;
import com.shuting.springsecurityoauth2.mapper.OauthUserMapper;
import com.shuting.springsecurityoauth2.service.OauthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component // 创建基于数据库的用户信息管理器
public class DBUserDetailsManager implements UserDetailsManager, UserDetailsPasswordService {
  @Autowired private OauthUserMapper oauthUserMapper;
  @Autowired private PasswordEncoder passwordEncoder;

  @Override
  public UserDetails updatePassword(UserDetails user, String newPassword) {
    return null;
  }

  // 从数据库中获取用户信息
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    QueryWrapper<OauthUser> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("username", username);
    OauthUser oauthUser = oauthUserMapper.selectOne(queryWrapper);
    if (oauthUser == null) {
      throw new UsernameNotFoundException(username);
    } else {
      return User.withUsername(oauthUser.getUsername())
          .password(oauthUser.getPassword())
          .disabled(oauthUser.getStatus() == 0)
          .credentialsExpired(oauthUser.getStatus() == 0)
          .accountLocked(oauthUser.getStatus() == 0)
              //用于角色授权
          //.roles("ADMIN")
              //用于基于权限授权，如果和前文的roles同时使用会覆盖roles
          .authorities(oauthUser.getAuthorities().split(","))
          .build();
    }
  }

  // 向数据库插入新用户
  @Override
  public void createUser(UserDetails userDetails) {
    if (userDetails.getUsername() == null) {
      throw new MissingPropertyException("username");
    }
    String password = userDetails.getPassword();
    if (password == null) {
      throw new MissingPropertyException("password");
    }
    QueryWrapper<OauthUser> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("username", userDetails.getUsername());
    if (oauthUserMapper.selectOne(queryWrapper) != null) {
      throw new ResourceAlreadyExistException("username already exists");
    }
    OauthUser oauthUser = new OauthUser();
    oauthUser.setUsername(userDetails.getUsername());
    oauthUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
    oauthUser.setClientId("1df7b0c1-aac0-148a14c7cba8");
    oauthUser.setAuthorities("ROLE_USER,ROLE_API");
    oauthUser.setStatus(1);
    oauthUserMapper.insert(oauthUser);
  }

  @Override
  public void updateUser(UserDetails userDetails) {}

  @Override
  public void deleteUser(String username) {}

  @Override
  public void changePassword(String oldPassword, String newPassword) {}

  @Override
  public boolean userExists(String username) {
    return false;
  }
}
