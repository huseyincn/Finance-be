package com.huseyincan.financeportfolio.config;

import com.huseyincan.financeportfolio.service.jwt.TokenManager;
import com.huseyincan.financeportfolio.util.UserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private TokenManager tokenManager;
    private UserDetailService userDetailService;

    @Autowired
    public JwtAuthorizationFilter(TokenManager tokenManager, UserDetailService userDetailService) {
        this.tokenManager = tokenManager;
        this.userDetailService = userDetailService;
    }

    @Override
    public void doFilterInternal(HttpServletRequest httpServletRequest,
                                 HttpServletResponse httpServletResponse,
                                 FilterChain filterChain) throws ServletException, IOException {
        final String tokenCore = httpServletRequest.getHeader("Authorization");
        String token = null;
        String username = null;
        if (tokenCore != null && tokenCore.contains("Bearer") && tokenCore.split(" ").length > 1) {
            token = tokenCore.split(" ")[1];
            try {
                username = tokenManager.getUserFromToken(token);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        if (token != null && username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (tokenManager.hasTokenValid(token)) {
                UserDetails user = this.userDetailService.loadUserByUsername(username);
                if (Objects.nonNull(user)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user,
                            null,
                            new ArrayList<>());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}