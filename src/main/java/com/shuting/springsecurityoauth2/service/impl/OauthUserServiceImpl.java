package com.shuting.springsecurityoauth2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuting.springsecurityoauth2.commons.config.securityConfig.DBUserDetailsManager;
import com.shuting.springsecurityoauth2.entity.user.OauthUser;
import com.shuting.springsecurityoauth2.mapper.OauthUserMapper;
import com.shuting.springsecurityoauth2.service.OauthUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OauthUserServiceImpl extends ServiceImpl<OauthUserMapper, OauthUser>
    implements OauthUserService {
  @Autowired private DBUserDetailsManager manager;
  @Autowired private OauthUserMapper oauthUserMapper;

  public void saveUserDetails(OauthUser oauthUser) {
    UserDetails userDetails =
        User.withUsername(oauthUser.getUsername())
            .password(oauthUser.getPassword())
            .roles("USER")
            .build();
    manager.createUser(userDetails);
  }
}
