package com.auction.virtual_auction_system.security;

import com.auction.virtual_auction_system.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StompJwtChannelInterceptorTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Test
    void authenticatesAStompConnectUsingTheJwtHeader() {
        StompJwtChannelInterceptor interceptor = interceptor();
        UserDetails details = User.withUsername("user@example.com")
                .password("ignored")
                .authorities("ROLE_USER")
                .build();
        when(jwtService.extractUsername("token"))
                .thenReturn("user@example.com");
        when(userService.loadUserByUsername("user@example.com"))
                .thenReturn(details);
        when(jwtService.isTokenValid("token", details)).thenReturn(true);

        StompHeaderAccessor accessor = StompHeaderAccessor.create(
                StompCommand.CONNECT);
        accessor.addNativeHeader("Authorization", "Bearer token");
        Message<?> message = message(accessor);

        Message<?> authenticatedMessage = interceptor.preSend(
                message,
                mock(MessageChannel.class));
        StompHeaderAccessor authenticatedAccessor =
                StompHeaderAccessor.wrap(authenticatedMessage);

        assertThat(authenticatedAccessor.getUser())
                .isInstanceOf(Authentication.class);
        assertThat(authenticatedAccessor.getUser().getName())
                .isEqualTo("user@example.com");
    }

    @Test
    void rejectsConnectWithoutAuthentication() {
        StompJwtChannelInterceptor interceptor = interceptor();
        StompHeaderAccessor accessor = StompHeaderAccessor.create(
                StompCommand.CONNECT);

        assertThatThrownBy(() -> interceptor.preSend(
                message(accessor),
                mock(MessageChannel.class)))
                .isInstanceOf(MessagingException.class)
                .hasMessageContaining("Authentication required");
    }

    @Test
    void allowsAuthenticatedAuctionTopicSubscriptionsOnly() {
        StompJwtChannelInterceptor interceptor = interceptor();
        StompHeaderAccessor accessor = StompHeaderAccessor.create(
                StompCommand.SUBSCRIBE);
        Authentication authentication = mock(Authentication.class);
        accessor.setUser(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        accessor.setDestination("/topic/auctions/42");

        interceptor.preSend(message(accessor), mock(MessageChannel.class));

        StompHeaderAccessor invalidAccessor = StompHeaderAccessor.create(
                StompCommand.SUBSCRIBE);
        invalidAccessor.setUser(authentication);
        invalidAccessor.setDestination("/topic/auctions/all");
        assertThatThrownBy(() -> interceptor.preSend(
                message(invalidAccessor),
                mock(MessageChannel.class)))
                .isInstanceOf(MessagingException.class)
                .hasMessageContaining("Invalid auction subscription");
    }

    private StompJwtChannelInterceptor interceptor() {
        return new StompJwtChannelInterceptor(jwtService, userService);
    }

    private Message<?> message(StompHeaderAccessor accessor) {
        return MessageBuilder.createMessage(
                new byte[0],
                accessor.getMessageHeaders());
    }
}
