package org.vttish.gymapplication.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";
  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
      String token = authHeader.substring(BEARER_PREFIX.length()).trim();
      if (!token.isBlank()) {
        try {
          authenticate(request, token);
        } catch (Exception ex) {
          SecurityContextHolder.clearContext();
        }
      }
    }

    filterChain.doFilter(request, response);
  }

  private void authenticate(HttpServletRequest request, String token) {
    UserPrincipal userPrincipal = jwtService.parseToken(token);

    List<SimpleGrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority("ROLE_" + userPrincipal.role().name())
    );

    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userPrincipal, null, authorities
    );
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
