package com.notification.dutynotifier.service.accountUserDetailsService;

import com.notification.dutynotifier.entity.user.User;
import com.notification.dutynotifier.repository.accountRepository.UserRepository;
import com.notification.dutynotifier.security.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(email));

        return new UserDetails(user);
    }
}