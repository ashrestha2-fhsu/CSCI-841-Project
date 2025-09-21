package CSCI_841_Project.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsServiceImpl userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    // JwtAuthenticationFilter.java
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getServletPath();
        // Skip all auth endpoints
        return p.startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String bearer = req.getHeader("Authorization");
        String token = (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;

        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsername(token);
            var userDetails = userDetailsService.loadUserByUsername(username);
            var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(req, res);
    }


//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String p = request.getServletPath();
//        return p.startsWith("/api/auth/"); // covers /register, /login, /verify, /auth/setup/**
//    }
//
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
//            throws ServletException, IOException {
//        String bearer = req.getHeader("Authorization");
//        String token = (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
//
//        if (token != null && jwtTokenProvider.validateToken(token)) {
//            String username = jwtTokenProvider.getUsername(token);
//            try {
//                UserDetails user = userDetailsService.loadUserByUsername(username);
//                var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
//                SecurityContextHolder.getContext().setAuthentication(auth);
//                System.out.println("[JWT] Authenticated as " + username + " with " + user.getAuthorities());
//            } catch (UsernameNotFoundException e) {
//                // If token references a non-existent user, do NOT blow up public requests.
//                System.out.println("[JWT] Token subject not found: " + username);
//                SecurityContextHolder.clearContext();
//            }
//        }
//
//        chain.doFilter(req, res);
//    }

}
