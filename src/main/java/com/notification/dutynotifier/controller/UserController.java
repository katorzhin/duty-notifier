package com.notification.dutynotifier.controller;

import com.notification.dutynotifier.dto.UserRequest;
import com.notification.dutynotifier.entity.User;
import com.notification.dutynotifier.service.UserService;
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
    public User create(@RequestBody @Valid UserRequest request) {
        return userService.create(request);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }
}