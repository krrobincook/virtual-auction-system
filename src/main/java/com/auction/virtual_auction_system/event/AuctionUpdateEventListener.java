package com.auction.virtual_auction_system.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AuctionUpdateEventListener {

    private final AuctionWebSocketPublisher publisher;

    public AuctionUpdateEventListener(AuctionWebSocketPublisher publisher) {
        this.publisher = publisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuctionUpdate(AuctionUpdateEvent event) {
        publisher.publish(event.message());
    }
}
