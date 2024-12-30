package com.shuting.springsecurityoauth2.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName oauth_user
 */
@TableName(value = "oauth_user")
@Data
public class OauthUser implements Serializable {

  @TableId private String username;
  private String password;
  private String clientId;
  private String authorities;
  private String phone;
  private String email;
  private String registerIp;
  private Integer status;

  @TableField(fill = FieldFill.INSERT)
  private Date createDate;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private Date updateDate;
}
