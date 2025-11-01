package CSCI_841_Project.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Value("${app.jwt-secret}")
    private String jwtSecret;


    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsServiceImpl userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getServletPath();
        // Skip all auth endpoints
        System.out.println("Checking if should filter: " + request.getServletPath());

        return p.startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String bearer = req.getHeader("Authorization");
        System.out.println("➡️ JwtFilter running for URI: " + req.getRequestURI());
        System.out.println("🪪 Raw Authorization header: " + bearer);

        String token = (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;

        if (StringUtils.hasText(token)) {
            boolean valid = jwtTokenProvider.validateToken(token);
            if (valid) {
                var username = jwtTokenProvider.getUsername(token);
                System.out.println("👤 Authenticated username: " + username);
                var userDetails = userDetailsService.loadUserByUsername(username);
                var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                System.out.println("🔐 Authorities: " + userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                System.out.println("⚠️ Token is invalid");
            }
        } else {
            System.out.println("🚫 No valid Bearer token found");
        }

        chain.doFilter(req, res);
    }

//    @Override
//    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
//            throws ServletException, IOException {
//        String bearer = req.getHeader("Authorization");
//        System.out.println("Authorization header on " + req.getRequestURI() + ": " + bearer);
//        String token = (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
//        try {
//            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
//                var userDetails = userDetailsService.loadUserByUsername(jwtTokenProvider.getUsername(token));
//                var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            }
//        } catch (Exception ignore) {
//            // DO NOT write 403 here—just fall through as anonymous
//        }
//        System.out.println("➡️ JwtFilter running for " + req.getRequestURI());
//
//        chain.doFilter(req, res);
//    }



    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts
                    .parserBuilder()
                    .setSigningKey(key()) // ✅ Use your base64-decoded secret key
                    .build()
                    .parseClaimsJws(token);

            System.out.println("✅ JWT is valid");
            return true;

        } catch (ExpiredJwtException e) {
            System.out.println("❌ Token expired at: " + e.getClaims().getExpiration());
        } catch (UnsupportedJwtException e) {
            System.out.println("❌ Unsupported JWT token");
        } catch (MalformedJwtException e) {
            System.out.println("❌ Malformed JWT token");
        } catch (SecurityException e) {
            System.out.println("❌ Invalid JWT signature");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ JWT claims string is empty");
        }

        return false;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }


}
