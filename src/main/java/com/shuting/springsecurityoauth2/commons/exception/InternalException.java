package com.shuting.springsecurityoauth2.commons.exception;

import com.shuting.springsecurityoauth2.commons.http.ApiError;

public class InternalException extends FlowException {
  public InternalException(String message) {
    super(ApiError.INTERNAL_ERROR, message);
  }
}
