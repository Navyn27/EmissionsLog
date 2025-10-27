package com.navyn.emissionlog.Config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.navyn.emissionlog.modules.users.services.JwtService;
import com.navyn.emissionlog.modules.users.services.UserDetailsServiceImpl;
import com.navyn.emissionlog.utils.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Skip filter for Swagger endpoints
        String path = request.getRequestURI();
        if (path.contains("/swagger-ui") || path.contains("/api-docs") ||
                path.contains("/v3/api-docs") || path.contains("/swagger-resources") ||
                path.contains("/webjars")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = request.getHeader("Authorization");
            String token = null;
            String username = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = jwtService.extractUserName(token);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = context.getBean(UserDetailsServiceImpl.class).loadUserByUsername(username);
                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource()
                            .buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
            
        } catch (ExpiredJwtException e) {
            handleException(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT token has expired");
        } catch (MalformedJwtException e) {
            handleException(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT token format");
        } catch (SignatureException e) {
            handleException(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT signature");
        } catch (UsernameNotFoundException e) {
            handleException(response, HttpServletResponse.SC_NOT_FOUND, "User not found: " + e.getMessage());
        } catch (Exception e) {
            handleException(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication error: " + e.getMessage());
        }
    }

    private void handleException(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        ApiResponse apiResponse = ApiResponse.error(message);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(apiResponse));
    }
}