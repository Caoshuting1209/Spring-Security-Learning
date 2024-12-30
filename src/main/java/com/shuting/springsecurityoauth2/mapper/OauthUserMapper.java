package com.shuting.springsecurityoauth2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuting.springsecurityoauth2.entity.user.OauthUser;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface OauthUserMapper extends BaseMapper<OauthUser> {}
