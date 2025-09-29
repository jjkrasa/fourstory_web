package com.fourstory.fourstory_api.controller;

import com.fourstory.fourstory_api.dto.request.EmailChangeRequest;
import com.fourstory.fourstory_api.dto.request.PasswordChangeRequest;
import com.fourstory.fourstory_api.dto.request.TokenRequest;
import com.fourstory.fourstory_api.model.tglobal.User;
import com.fourstory.fourstory_api.service.EmailChangeService;
import com.fourstory.fourstory_api.service.PasswordChangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final PasswordChangeService passwordChangeService;

    private final EmailChangeService emailChangeService;

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PasswordChangeRequest request
    ) {
        passwordChangeService.changePassword(user, request.getCurrentPassword(), request.getNewPassword());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/email-change/request")
    public ResponseEntity<Void> requestEmailChange(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody EmailChangeRequest request
    ) {
        emailChangeService.requestEmailChange(user, request.getEmail());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/email-change/confirm")
    public ResponseEntity<Void> confirmEmailChange(@Valid @RequestBody TokenRequest request) {
        emailChangeService.confirmEmailChange(request.getToken());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
