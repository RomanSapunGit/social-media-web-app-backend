package com.roman.sapun.java.socialmedia.security.filter;

import com.roman.sapun.java.socialmedia.security.UserDetailsServiceImpl;
import com.roman.sapun.java.socialmedia.service.JwtAuthService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtAuthService jwtAuthService;
    private final UserDetailsServiceImpl userDetailsService;
    private final HandlerExceptionResolver resolver;


    @Autowired
    public JwtAuthFilter(JwtAuthService jwtAuthService, UserDetailsServiceImpl userDetailsService, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtAuthService = jwtAuthService;
        this.userDetailsService = userDetailsService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        try {
            String token = null;
            String username = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                String usernameToMatch = jwtAuthService.validateAndGetUsername(token);
                username = usernameToMatch == null ?
                        jwtAuthService.extractUsername(token) :
                        usernameToMatch;
            }
            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (token.length() < 150) {
                    if (jwtAuthService.validateToken(token, userDetails.getUsername()) &&
                            SecurityContextHolder.getContext().getAuthentication() == null) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | GeneralSecurityException | DisabledException | UnsupportedJwtException e) {
            resolver.resolveException(request, response, null, e);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/account");
    }
}
