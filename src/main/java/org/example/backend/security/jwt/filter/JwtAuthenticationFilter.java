package org.example.backend.security.jwt.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.backend.security.CustomUserDetails;
import org.example.backend.security.jwt.JwtProvider;
import org.example.backend.security.jwt.authenticationToken.JwtAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 토큰 얻어오기, 토큰 파싱, 시큐리티컨텍홀더에 인증정보 넣기.

        String token = getToken(request);

        if(token!=null){
            try {

                Claims claims = jwtProvider.parseToken(token);

                Long userId = Long.valueOf(claims.getSubject());
                String email = claims.get("email", String.class);
                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

                CustomUserDetails principal = new CustomUserDetails(userId, email, authorities);

                Authentication authentication = new JwtAuthenticationToken(principal.getAuthorities(), principal, null);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            catch (JwtException | IllegalArgumentException e){
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request,response);
    }

    private String getToken(HttpServletRequest request){
        String bearer = request.getHeader(HEADER);
        if (bearer != null && bearer.startsWith(PREFIX)) {
            return bearer.substring(PREFIX.length());
        }
        return null;
    }
}
