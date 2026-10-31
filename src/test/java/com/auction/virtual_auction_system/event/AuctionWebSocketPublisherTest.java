package com.auction.virtual_auction_system.event;

import com.auction.virtual_auction_system.controller.dto.AuctionUpdateMessage;
import com.auction.virtual_auction_system.entity.AuctionEventType;
import com.auction.virtual_auction_system.entity.AuctionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuctionWebSocketPublisherTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Test
    void publishesUpdatesToTheAuctionSpecificTopic() {
        AuctionWebSocketPublisher publisher =
                new AuctionWebSocketPublisher(messagingTemplate);
        AuctionUpdateMessage message = new AuctionUpdateMessage(
                AuctionEventType.BID_PLACED,
                7L,
                new BigDecimal("35000.00"),
                25L,
                3L,
                "Amit",
                new BigDecimal("35000.00"),
                LocalDateTime.of(2026, 10, 31, 15, 30),
                AuctionStatus.ACTIVE,
                LocalDateTime.of(2026, 10, 31, 15, 30));

        publisher.publish(message);

        verify(messagingTemplate).convertAndSend(
                eq("/topic/auctions/7"),
                eq(message));
    }

    @Test
    void brokerFailureDoesNotEscapeThePublisher() {
        AuctionWebSocketPublisher publisher =
                new AuctionWebSocketPublisher(messagingTemplate);
        AuctionUpdateMessage message = new AuctionUpdateMessage();
        message.setAuctionId(7L);
        message.setEventType(AuctionEventType.AUCTION_ENDED);
        org.mockito.Mockito.doThrow(new RuntimeException("broker unavailable"))
                .when(messagingTemplate)
                .convertAndSend(eq("/topic/auctions/7"), eq(message));

        publisher.publish(message);
    }
}
