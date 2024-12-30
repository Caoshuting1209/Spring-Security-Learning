package com.shuting.springsecurityoauth2.commons.exception;

import com.shuting.springsecurityoauth2.commons.http.ApiError;

public class ResourceNotFoundException extends FlowException {
  public ResourceNotFoundException(String name, Object value) {
    super(ApiError.RESOURCE_NOT_FOUND, String.format("Resource %s %s not found", name, value));
  }
}
