package com.notification.dutynotifier.controller.authController;

import com.notification.dutynotifier.dto.auth.LoginRequest;
import com.notification.dutynotifier.dto.auth.LoginResponse;
import com.notification.dutynotifier.service.authService.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody
            @Valid
            LoginRequest request) {
        return authService.login(request);
    }
}