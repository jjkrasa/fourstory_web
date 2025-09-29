package com.fourstory.fourstory_api.controller;

import com.fourstory.fourstory_api.dto.request.*;
import com.fourstory.fourstory_api.service.AuthService;
import com.fourstory.fourstory_api.service.EmailVerificationService;
import com.fourstory.fourstory_api.service.PasswordResetService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final EmailVerificationService emailVerificationService;

    private final PasswordResetService passwordResetService;

    @Value("${app.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request, HttpServletResponse httpResponse) {
        String accessToken = authService.login(request);

        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(accessTokenExpiration / 1000)
                .build();

        httpResponse.addHeader("Set-Cookie", accessTokenCookie.toString());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody TokenRequest request) {
        emailVerificationService.verifyEmail(request.getToken());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerificationEmail(@Valid @RequestBody ResendVerificationEmailRequest request) {
        emailVerificationService.resendVerificationEmail(request.getEmail());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<Void> requestForgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestForgotPassword(request.getEmail());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<Void> forgotPasswordConfirm(@Valid @RequestBody ForgotPasswordConfirmRequest request) {
        passwordResetService.confirmPasswordResetChange(request.getToken(), request.getNewPassword());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
