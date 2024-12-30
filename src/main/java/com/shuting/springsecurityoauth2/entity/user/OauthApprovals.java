package com.shuting.springsecurityoauth2.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName oauth_approvals
 */
@TableName(value = "oauth_approvals")
@Data
public class OauthApprovals implements Serializable {
  private String userid;
  private String clientid;
  private String scope;
  private String status;
  private Date expiresat;
  private Date lastmodifiedat;
}
