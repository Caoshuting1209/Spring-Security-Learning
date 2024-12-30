package com.shuting.springsecurityoauth2.entity.user;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName oauth_access_token
 */
@TableName(value = "oauth_access_token")
@Data
public class OauthAccessToken implements Serializable {
  @TableId private String authenticationId;
  private String tokenId;
  private String userName;
  private String clientId;
  private String refreshToken;
  private byte[] token;
  private byte[] authentication;
}
