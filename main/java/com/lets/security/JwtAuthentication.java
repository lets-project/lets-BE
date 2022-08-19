package com.lets.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


public class JwtAuthentication extends AbstractAuthenticationToken {

    private UserPrincipal principal;


    public JwtAuthentication(UserPrincipal principal){
        super(principal.getAuthorities());
        this.principal = principal;

    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return super.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}