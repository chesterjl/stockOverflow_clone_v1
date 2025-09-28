package com.lauzon.stackOverflow.service.impl;

import com.lauzon.stackOverflow.entity.UserEntity;
import com.lauzon.stackOverflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Account is not found with email: " + email));
        return User.builder()
                .username(existingUser.getEmail())
                .password(existingUser.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + existingUser.getRole().name()))
                .build();
    }
}
