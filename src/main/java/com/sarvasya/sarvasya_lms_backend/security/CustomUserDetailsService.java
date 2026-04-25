package com.sarvasya.sarvasya_lms_backend.security;

import com.sarvasya.sarvasya_lms_backend.model.user.BaseUser;
import com.sarvasya.sarvasya_lms_backend.model.user.User;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.sarvasya.sarvasya_lms_backend.repository.user.GlobalUserRepository;
import com.sarvasya.sarvasya_lms_backend.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;
    private final GlobalUserRepository globalUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BaseUser user;
        String currentTenant = TenantContext.getTenantId();

        if ("tenant".equals(currentTenant)) {
            // In global context, use globalUserRepository directly to avoid column mismatch errors
            user = globalUserRepository.findByEmail(username).orElse(null);
        } else {
            // First try current tenant schema
            user = userRepository.findByEmail(username).orElse(null);

            // Fallback: If not found in tenant, check global schema
            if (user == null) {
                user = globalUserRepository.findByEmail(username).orElse(null);
            }
        }


        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        String authority = (user.getRole() != null) ? user.getRole().getValue() : "user";
        log.debug("Loading user {} with authority {}", user.getEmail(), authority);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getIsActive() != null ? user.getIsActive() : true, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                Collections.singletonList(new SimpleGrantedAuthority(authority))
        );
    }
}








