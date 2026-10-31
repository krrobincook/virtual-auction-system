package com.auction.virtual_auction_system.service;

import com.auction.virtual_auction_system.controller.dto.AuctionUpdateMessage;
import com.auction.virtual_auction_system.entity.Auction;
import com.auction.virtual_auction_system.entity.AuctionEventType;
import com.auction.virtual_auction_system.entity.AuctionStatus;
import com.auction.virtual_auction_system.event.AuctionUpdateEvent;
import com.auction.virtual_auction_system.repository.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Moves auctions through their time-based lifecycle without requiring a user
 * request to trigger the transition.
 */
@Component
public class AuctionScheduler {

    private static final long STATUS_UPDATE_INTERVAL_MILLIS = 60_000L;

    private final AuctionRepository auctionRepository;
    private ApplicationEventPublisher eventPublisher;

    public AuctionScheduler(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Runs once per minute. The comparison is inclusive at both boundaries:
     * an auction starts at startTime and ends at endTime.
     */
    @Scheduled(fixedRate = STATUS_UPDATE_INTERVAL_MILLIS)
    @Transactional
    public void updateAuctionStatuses() {
        updateAuctionStatuses(LocalDateTime.now());
    }

    /**
     * Package-visible time-aware entry point used to keep the transition rules
     * deterministic in tests.
     */
    void updateAuctionStatuses(LocalDateTime now) {
        List<Auction> changedAuctions = new ArrayList<>();
        List<AuctionUpdateEvent> events = new ArrayList<>();

        for (Auction auction : auctionRepository
                .findByStatusAndStartTimeLessThanEqual(
                        AuctionStatus.PENDING, now)) {
            if (auction.getEndTime() != null
                    && !now.isBefore(auction.getEndTime())) {
                auction.setStatus(AuctionStatus.ENDED);
                events.add(eventFor(
                        AuctionEventType.AUCTION_ENDED, auction, now));
            } else {
                auction.setStatus(AuctionStatus.ACTIVE);
                events.add(eventFor(
                        AuctionEventType.AUCTION_STARTED, auction, now));
            }
            changedAuctions.add(auction);
        }

        for (Auction auction : auctionRepository
                .findByStatusAndEndTimeLessThanEqual(
                        AuctionStatus.ACTIVE, now)) {
            auction.setStatus(AuctionStatus.ENDED);
            changedAuctions.add(auction);
            events.add(eventFor(
                    AuctionEventType.AUCTION_ENDED, auction, now));
        }

        if (!changedAuctions.isEmpty()) {
            auctionRepository.saveAll(changedAuctions);
            if (eventPublisher != null) {
                events.forEach(eventPublisher::publishEvent);
            }
        }
    }

    private AuctionUpdateEvent eventFor(
            AuctionEventType eventType,
            Auction auction,
            LocalDateTime timestamp) {
        return new AuctionUpdateEvent(
                new AuctionUpdateMessage(
                        eventType,
                        auction.getId(),
                        auction.getCurrentPrice(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        auction.getStatus(),
                        timestamp));
    }
}
