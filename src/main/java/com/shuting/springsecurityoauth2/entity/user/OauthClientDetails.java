package com.shuting.springsecurityoauth2.entity.user;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName oauth_client_details
 */
@TableName(value = "oauth_client_details")
@Data
public class OauthClientDetails implements Serializable {
  @TableId private String clientId;
  private String resourceIds;
  private String clientSecret;
  private String scope;
  private String authorizedGrantTypes;
  private String webServerRedirectUri;
  private String authorities;
  private Integer accessTokenValidity;
  private Integer refreshTokenValidity;
  private String additionalInformation;
  private String autoapprove;
}
