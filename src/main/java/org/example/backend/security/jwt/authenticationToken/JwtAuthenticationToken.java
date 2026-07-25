package org.example.backend.security.jwt.authenticationToken;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private Object principal;
    private Object credential;

    // 검증 성공 후
    public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Object principal, Object credential){
        super(authorities);
        this.principal = principal;
        this.credential = credential;
        setAuthenticated(true);
    }

    // 미인증
    public JwtAuthenticationToken(String token) {
        super((Collection<? extends GrantedAuthority>) null);
        this.principal = null;
        this.credential = token;
        setAuthenticated(false);
    }

    @Override
    public @Nullable Object getCredentials() {
        return this.credential;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return this.principal;
    }
}
