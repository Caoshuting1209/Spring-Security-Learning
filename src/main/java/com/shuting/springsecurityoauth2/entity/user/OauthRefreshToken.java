package com.shuting.springsecurityoauth2.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName oauth_refresh_token
 */
@TableName(value = "oauth_refresh_token")
@Data
public class OauthRefreshToken implements Serializable {
  private String tokenId;
  private byte[] token;
  private byte[] authentication;
}
