package com.shuting.springsecurityoauth2.commons.exception;

import com.shuting.springsecurityoauth2.commons.http.ApiError;

public class ResourceAlreadyExistException extends FlowException {
  public ResourceAlreadyExistException(String name, Object value) {
    super(ApiError.RESOURCE_ALREADY_EXIST, String.format("%s %s already exists", name, value));
  }

  public ResourceAlreadyExistException(String message) {
    super(ApiError.RESOURCE_ALREADY_EXIST, message);
  }
}
