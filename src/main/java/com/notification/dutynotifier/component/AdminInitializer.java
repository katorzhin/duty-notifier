package com.notification.dutynotifier.component;

import com.notification.dutynotifier.entity.user.User;
import com.notification.dutynotifier.entity.user.Role;
import com.notification.dutynotifier.repository.accountRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.findByEmail("admin@test.com").isEmpty()) {

            User admin = User.builder()
                    .email("admin@test.com")
                    .password(passwordEncoder.encode("123456"))
                    .role(Role.ADMIN)
                    .systemAdmin(true)
                    .build();

            userRepository.save(admin);
        }
    }
}