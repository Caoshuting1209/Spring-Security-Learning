package com.shuting.springsecurityoauth2.entity.user;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName oauth_client_token
 */
@TableName(value = "oauth_client_token")
@Data
public class OauthClientToken implements Serializable {
  @TableId private String authenticationId;
  private String tokenId;
  private String userName;
  private String clientId;
  private byte[] token;
}
