package com.int_solutions.sys.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImp userDetailsServiceImp;
    private final TokenBlacklistService tokenBlacklistService;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
            ) throws ServletException, IOException {
                final String authHeader= request.getHeader("Authorization");
                final String jwt;
                final  String username;

                if (authHeader == null || !authHeader.startsWith("Bearer")){
                    filterChain.doFilter(request, response);
                    return;
                }
                jwt= authHeader.substring(7);

                //Valida si el token esta en la lista negra
        if(tokenBlacklistService.estaRevocado(jwt)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"mensaje\": \"El token ha sido revocado. Por favor, inicie sesión nuevamente.\"}");
            return;
        }
                try {
                    username= jwtUtil.extractUsername(jwt);
                    if(username != null && SecurityContextHolder.getContext().getAuthentication()== null){
                        UserDetails userDetails = this.userDetailsServiceImp.loadUserByUsername(username);
                        if(jwtUtil.validateToken(jwt, userDetails)){
                            UsernamePasswordAuthenticationToken authToken= new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    }
                }catch (Exception e){
                    logger.warn("Token JWT no valido o expirado: " + e.getMessage());
                }
                filterChain.doFilter(request,response);
    }
}
