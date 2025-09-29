package com.gondo.map.domain.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@AllArgsConstructor
public enum UserRole implements GrantedAuthority {
    ADMIN,
    MEMBER;

    @Override
    public String getAuthority() {
        return name();
    }
}
