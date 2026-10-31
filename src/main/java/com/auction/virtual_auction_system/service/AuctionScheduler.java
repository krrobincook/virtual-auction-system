package com.auction.virtual_auction_system.service;

import com.auction.virtual_auction_system.entity.Auction;
import com.auction.virtual_auction_system.entity.AuctionStatus;
import com.auction.virtual_auction_system.repository.AuctionRepository;
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

    public AuctionScheduler(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
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

        for (Auction auction : auctionRepository
                .findByStatusAndStartTimeLessThanEqual(
                        AuctionStatus.PENDING, now)) {
            if (auction.getEndTime() != null
                    && !now.isBefore(auction.getEndTime())) {
                auction.setStatus(AuctionStatus.ENDED);
            } else {
                auction.setStatus(AuctionStatus.ACTIVE);
            }
            changedAuctions.add(auction);
        }

        for (Auction auction : auctionRepository
                .findByStatusAndEndTimeLessThanEqual(
                        AuctionStatus.ACTIVE, now)) {
            auction.setStatus(AuctionStatus.ENDED);
            changedAuctions.add(auction);
        }

        if (!changedAuctions.isEmpty()) {
            auctionRepository.saveAll(changedAuctions);
        }
    }
}
