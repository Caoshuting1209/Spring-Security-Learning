package com.shuting.springsecurityoauth2.commons.exception;

import com.shuting.springsecurityoauth2.commons.http.ApiError;

public class InvalidStatusException extends FlowException {
  public InvalidStatusException(String message) {
    super(ApiError.INVALID_STATUS, message);
  }
}
