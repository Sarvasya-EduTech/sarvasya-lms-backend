package com.sarvasya.sarvasya_lms_backend.security;

import com.sarvasya.sarvasya_lms_backend.model.User;
import com.sarvasya.sarvasya_lms_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // First try to find user in the current context schema
        User user = userRepository.findByEmail(username).orElse(null);

        // Fallback: If not found in current schema, check the central 'tenant' schema
        // This allows TENANT_MANAGERs (who live in 'tenant' schema) to access tenant-specific APIs
        if (user == null && !"tenant".equals(TenantContext.getTenantId())) {
            String originalTenant = TenantContext.getTenantId();
            try {
                TenantContext.setTenantId("tenant");
                user = userRepository.findByEmail(username).orElse(null);
            } finally {
                TenantContext.setTenantId(originalTenant);
            }
        }

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        String authority = (user.getRole() != null) ? user.getRole().getValue() : "user";
        System.out.println(">>> Loading user: " + user.getEmail() + " with authority: " + authority);

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
