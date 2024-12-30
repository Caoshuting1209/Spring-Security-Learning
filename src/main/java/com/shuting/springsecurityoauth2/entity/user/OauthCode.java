package com.shuting.springsecurityoauth2.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName oauth_code
 */
@TableName(value = "oauth_code")
@Data
public class OauthCode implements Serializable {
  private String code;
  private byte[] authentication;
}
