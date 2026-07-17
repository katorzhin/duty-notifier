package com.notification.dutynotifier.dto.response;

import com.notification.dutynotifier.entity.user.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private Role role;
    private boolean systemAdmin;
}