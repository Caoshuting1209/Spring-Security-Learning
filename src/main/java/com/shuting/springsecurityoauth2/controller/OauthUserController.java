package com.shuting.springsecurityoauth2.controller;

import com.shuting.springsecurityoauth2.commons.http.Result;
import com.shuting.springsecurityoauth2.entity.user.OauthUser;

import com.shuting.springsecurityoauth2.service.OauthUserService;
import com.shuting.springsecurityoauth2.service.impl.OauthUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class OauthUserController {
  @Autowired private OauthUserServiceImpl oauthUserService;

  // 获取当前用户信息
  @GetMapping("/information")
  public Map index() {
    SecurityContext context = SecurityContextHolder.getContext();
    Authentication authentication = context.getAuthentication();
    Object principal = authentication.getPrincipal();
    Object credentials = authentication.getCredentials(); // 脱敏
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    HashMap result = new HashMap();
    result.put("username", authentication.getName());
    result.put("authorities", authorities);
    return result;
  }

  // 获取用户列表
  @GetMapping("/list")
  @PreAuthorize("hasAuthority('ROLE_API')") // 在开启方法授权的情况下，这里可以用于校验请求是否被授权
  public List<OauthUser> getOauthUser() {
    return oauthUserService.list();
  }

  // 新增用户
  @PostMapping("/add")
  //没有授权校验默认对所有用户授权
  public void addUser(@RequestBody OauthUser oauthUser) {
    oauthUserService.saveUserDetails(oauthUser);
  }
}
