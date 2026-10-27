package com.auction.virtual_auction_system.security;
import com.auction.virtual_auction_system.service.Impl.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;

    private final UserServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserServiceImpl userDetailsService){
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Read Authorization header
        final String authHeader =
                request.getHeader("Authorization");

        /*
         * If Authorization header does not exist
         * or does not start with Bearer,
         * continue without authentication.
         */
        if (authHeader == null
                || !authHeader.regionMatches(
                true, 0, "Bearer ", 0, "Bearer ".length())) {

            filterChain.doFilter(request, response);
            return;
        }

        // Remove "Bearer " from token
        final String jwt = authHeader.substring("Bearer ".length()).trim();

        if (jwt.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            // Extract email from JWT
            final String userEmail =
                    jwtService.extractUsername(jwt);

            /*
             * Only authenticate if there isn't already
             * an authenticated user in SecurityContext.
             */
            if (userEmail != null
                    && SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null) {

                // Load user from database
                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(userEmail);

                // Verify JWT
                if (jwtService.isTokenValid(
                        jwt,
                        userDetails)) {

                    /*
                     * Create Spring Security authentication object.
                     */
                    UsernamePasswordAuthenticationToken
                            authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // Attach request information
                    authentication
                            .setDetails(
                                    new WebAuthenticationDetailsSource()
                                            .buildDetails(request)
                            );

                    /*
                     * Tell Spring Security that
                     * this request is authenticated.
                     */
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }

        } catch (Exception e) {

            logger.warn(
                    "JWT authentication failed for {}: {}",
                    request.getRequestURI(),
                    e.getMessage()
            );

            /*
             * Invalid/expired JWT.
             *
             * We don't directly send an error here.
             * Spring Security will handle the request
             * according to the security configuration.
             */
        }

        // Continue request processing
        filterChain.doFilter(request, response);
    }
}
