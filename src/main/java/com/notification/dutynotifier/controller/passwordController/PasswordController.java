package com.notification.dutynotifier.controller.passwordController;

import com.notification.dutynotifier.dto.user.ChangePasswordRequest;
import com.notification.dutynotifier.dto.user.ResetPasswordRequest;
import com.notification.dutynotifier.service.passwordService.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/password")
public class PasswordController {

    private final PasswordService passwordService;

    @PatchMapping("/change")
    public void changePassword(@RequestBody @Valid
                               ChangePasswordRequest request) {

        passwordService.changePassword(request);
    }

    @PatchMapping("/reset/{id}")
    public void resetPassword(@PathVariable Long id, @RequestBody @Valid
    ResetPasswordRequest request) {
        passwordService.resetPassword(id, request);
    }
}