package com.fourstory.fourstory_api.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    LINK_INVALID_OR_EXPIRED("This link is invalid or has expired", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED("You don't have permission to perform this action", HttpStatus.FORBIDDEN),
    INVALID_INPUT("Invalid input", HttpStatus.BAD_REQUEST),
    WRONG_CURRENT_PASSWORD("Invalid password", HttpStatus.BAD_REQUEST),
    PASSWORDS_DO_NOT_MATCH("Passwords do not match", HttpStatus.BAD_REQUEST),
    PASSWORD_SAME_AS_OLD("New password must differ from the old one", HttpStatus.BAD_REQUEST),
    USERNAME_ALREADY_IN_USE("Username is already used", HttpStatus.CONFLICT),
    EMAIL_ALREADY_IN_USE("Email is already used", HttpStatus.CONFLICT),
    AUTHENTICATION_FAILED("Invalid email or password", HttpStatus.UNAUTHORIZED),
    EMAIL_NOT_VERIFIED("Please verify your email to sign in", HttpStatus.UNAUTHORIZED),
    INTERNAL_SERVER_ERROR("An internal server error occurred", HttpStatus.INTERNAL_SERVER_ERROR)
    ;

    private final String message;
    private final HttpStatus httpStatus;
}
