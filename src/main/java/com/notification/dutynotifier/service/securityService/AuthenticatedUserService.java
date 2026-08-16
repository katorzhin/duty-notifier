package com.notification.dutynotifier.service.securityService;

import com.notification.dutynotifier.entity.user.User;
import com.notification.dutynotifier.repository.accountRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        return userRepository.findByEmail(getCurrentUserEmail())
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    public String getCurrentUserEmail() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return authentication.getName();
    }
}