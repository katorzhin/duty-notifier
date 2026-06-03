package com.notification.dutynotifier.controller.userController;

import com.notification.dutynotifier.dto.response.UserResponse;
import com.notification.dutynotifier.dto.userRequest.UserRequest;
import com.notification.dutynotifier.entity.user.User;
import com.notification.dutynotifier.service.userService.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponse create(@RequestBody @Valid UserRequest request) {
        return userService.create(request);
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAll();
    }
}