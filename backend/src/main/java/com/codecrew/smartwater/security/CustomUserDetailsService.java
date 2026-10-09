package com.codecrew.smartwater.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Foundational placeholder to satisfy dependency injection; wired to UserRepository in Module 1
        throw new UsernameNotFoundException("User not found: " + username);
    }
}
