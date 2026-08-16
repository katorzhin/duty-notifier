package com.notification.dutynotifier.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;

}