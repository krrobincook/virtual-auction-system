package com.auction.virtual_auction_system.event;

import com.auction.virtual_auction_system.controller.dto.AuctionUpdateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuctionWebSocketPublisher {

    private static final Logger log = LoggerFactory.getLogger(
            AuctionWebSocketPublisher.class);

    private final SimpMessagingTemplate messagingTemplate;

    public AuctionWebSocketPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publish(AuctionUpdateMessage message) {
        if (message == null || message.getAuctionId() == null) {
            log.warn("Skipping auction update with no auction id");
            return;
        }

        String destination = "/topic/auctions/" + message.getAuctionId();
        try {
            messagingTemplate.convertAndSend(destination, message);
            log.debug("Published {} for auction {}",
                    message.getEventType(), message.getAuctionId());
        } catch (RuntimeException exception) {
            // A broker failure must not roll back an already committed REST
            // transaction or make a successful bid appear unsuccessful.
            log.error("Unable to publish {} for auction {}",
                    message.getEventType(), message.getAuctionId(), exception);
        }
    }
}
