package com.notification.dutynotifier.dto.user;

import com.notification.dutynotifier.entity.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    @Email
    @NotBlank
    private String email;

    @NotNull
    private Role role;
}