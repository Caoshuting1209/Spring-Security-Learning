package com.shuting.springsecurityoauth2.commons.http;

import lombok.Data;

@Data
public class PageRequest {
  private int page = 1;
  private int limit = 5;
}
