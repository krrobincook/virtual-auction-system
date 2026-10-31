package com.auction.virtual_auction_system.security;

import com.auction.virtual_auction_system.service.UserService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Authenticates STOMP CONNECT frames and prevents anonymous or invalid topic
 * subscriptions. JWTs are supplied as STOMP CONNECT headers, not URL query
 * parameters.
 */
@Component
public class StompJwtChannelInterceptor implements ChannelInterceptor {

    private static final String AUCTION_TOPIC_PREFIX = "/topic/auctions/";

    private final JwtService jwtService;
    private final UserService userService;

    public StompJwtChannelInterceptor(
            JwtService jwtService,
            UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = (StompHeaderAccessor)
                MessageHeaderAccessor.getMutableAccessor(message);

        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        if (accessor.getCommand() == StompCommand.CONNECT
                || accessor.getCommand() == StompCommand.STOMP) {
            authenticate(accessor, message);
            return MessageBuilder.createMessage(
                    message.getPayload(),
                    accessor.getMessageHeaders());
        } else if (accessor.getCommand() == StompCommand.SUBSCRIBE) {
            authorizeSubscription(accessor, message);
        }

        return message;
    }

    private void authenticate(
            StompHeaderAccessor accessor,
            Message<?> message) {
        String authorization = accessor.getFirstNativeHeader("Authorization");
        if (authorization == null) {
            authorization = accessor.getFirstNativeHeader("authorization");
        }

        if (authorization == null
                || !authorization.regionMatches(
                true, 0, "Bearer ", 0, "Bearer ".length())) {
            throw authenticationRequired(message);
        }

        String token = authorization.substring("Bearer ".length()).trim();
        if (token.isBlank()) {
            throw authenticationRequired(message);
        }

        try {
            String email = jwtService.extractUsername(token);
            UserDetails userDetails = userService.loadUserByUsername(email);
            if (!jwtService.isTokenValid(token, userDetails)) {
                throw authenticationRequired(message);
            }

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
            accessor.setUser(authentication);
        } catch (RuntimeException exception) {
            throw authenticationRequired(message);
        }
    }

    private void authorizeSubscription(
            StompHeaderAccessor accessor,
            Message<?> message) {
        if (!(accessor.getUser() instanceof Authentication authentication)
                || !authentication.isAuthenticated()) {
            throw authenticationRequired(message);
        }

        String destination = accessor.getDestination();
        if (destination == null
                || !destination.matches("/topic/auctions/[0-9]+")) {
            throw new MessagingException("Invalid auction subscription");
        }
    }

    private MessagingException authenticationRequired(Message<?> message) {
        return new MessagingException(message, "Authentication required");
    }
}
