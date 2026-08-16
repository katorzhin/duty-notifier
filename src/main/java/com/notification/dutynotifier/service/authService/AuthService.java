package com.notification.dutynotifier.service.authService;

import com.notification.dutynotifier.dto.auth.LoginRequest;
import com.notification.dutynotifier.dto.auth.LoginResponse;
import com.notification.dutynotifier.entity.user.User;
import com.notification.dutynotifier.entity.auditLog.AuditAction;
import com.notification.dutynotifier.entity.auditLog.messages.SystemAuditMessages;
import com.notification.dutynotifier.repository.accountRepository.UserRepository;
import com.notification.dutynotifier.service.auditLogService.AuditLogService;
import com.notification.dutynotifier.service.jwtService.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuditLogService auditLogService;

    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() -> new RuntimeException(
                                        "Invalid credentials"));

        boolean matches = passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword());

        if (!matches) {
            throw new RuntimeException("Invalid credentials");
        }

        auditLogService.log(
                "SYSTEM",
                AuditAction.LOGIN,
                SystemAuditMessages.login(user.getEmail()));

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(token, user.getRole().name());
    }
}