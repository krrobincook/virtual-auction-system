package com.auction.virtual_auction_system.service;

import com.auction.virtual_auction_system.entity.Auction;
import com.auction.virtual_auction_system.entity.AuctionStatus;
import com.auction.virtual_auction_system.entity.Bid;
import com.auction.virtual_auction_system.event.AuctionUpdateEvent;
import com.auction.virtual_auction_system.repository.AuctionRepository;
import com.auction.virtual_auction_system.repository.BidRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AuctionSchedulerTest {

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private BidRepository bidRepository;

    @InjectMocks
    private AuctionScheduler auctionScheduler;

    @BeforeEach
    void setUp() {
        auctionScheduler.setEventPublisher(eventPublisher);
        auctionScheduler.setBidRepository(bidRepository);
    }

    @Test
    void activatesPendingAuctionsAtStartTimeAndEndsExpiredAuctions() {
        LocalDateTime now = LocalDateTime.of(2026, 10, 31, 12, 0);
        Auction pending = auctionWithTimes(
                now.minusMinutes(1), now.plusMinutes(10), AuctionStatus.PENDING);
        Auction active = auctionWithTimes(
                now.minusMinutes(10), now, AuctionStatus.ACTIVE);

        when(auctionRepository.findByStatusAndStartTimeLessThanEqual(
                AuctionStatus.PENDING, now)).thenReturn(List.of(pending));
        when(auctionRepository.findByStatusAndEndTimeLessThanEqual(
                AuctionStatus.ACTIVE, now)).thenReturn(List.of(active));

        auctionScheduler.updateAuctionStatuses(now);

        assertThat(pending.getStatus()).isEqualTo(AuctionStatus.ACTIVE);
        assertThat(active.getStatus()).isEqualTo(AuctionStatus.ENDED);

        ArgumentCaptor<List<Auction>> captor = ArgumentCaptor.forClass(List.class);
        verify(auctionRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).containsExactly(pending, active);
        verify(eventPublisher, times(2)).publishEvent(any(AuctionUpdateEvent.class));
    }

    @Test
    void marksPendingAuctionEndedWhenItMissedBothBoundaries() {
        LocalDateTime now = LocalDateTime.of(2026, 10, 31, 12, 0);
        Auction expiredPending = auctionWithTimes(
                now.minusHours(2), now.minusHours(1), AuctionStatus.PENDING);

        when(auctionRepository.findByStatusAndStartTimeLessThanEqual(
                AuctionStatus.PENDING, now)).thenReturn(List.of(expiredPending));
        when(auctionRepository.findByStatusAndEndTimeLessThanEqual(
                AuctionStatus.ACTIVE, now)).thenReturn(List.of());

        auctionScheduler.updateAuctionStatuses(now);

        assertThat(expiredPending.getStatus()).isEqualTo(AuctionStatus.ENDED);
        verify(auctionRepository).saveAll(List.of(expiredPending));
        verify(eventPublisher).publishEvent(any(AuctionUpdateEvent.class));
    }

    @Test
    void recordsTheHighestBidderWhenAnAuctionEnds() {
        LocalDateTime now = LocalDateTime.of(2026, 10, 31, 12, 0);
        Auction auction = auctionWithTimes(
                now.minusHours(1), now, AuctionStatus.ACTIVE);
        auction.setId(11L);

        com.auction.virtual_auction_system.entity.User winner =
                new com.auction.virtual_auction_system.entity.User();
        winner.setId(5L);
        winner.setName("Amit");

        Bid winningBid = new Bid();
        winningBid.setAmount(new BigDecimal("35000.00"));
        winningBid.setBidder(winner);

        when(auctionRepository.findByStatusAndStartTimeLessThanEqual(
                AuctionStatus.PENDING, now)).thenReturn(List.of());
        when(auctionRepository.findByStatusAndEndTimeLessThanEqual(
                AuctionStatus.ACTIVE, now)).thenReturn(List.of(auction));
        when(bidRepository.findTopByAuctionIdOrderByAmountDesc(11L))
                .thenReturn(Optional.of(winningBid));

        auctionScheduler.updateAuctionStatuses(now);

        assertThat(auction.getWinner()).isSameAs(winner);
        verify(bidRepository).findTopByAuctionIdOrderByAmountDesc(11L);
    }

    private Auction auctionWithTimes(
            LocalDateTime startTime,
            LocalDateTime endTime,
            AuctionStatus status) {
        Auction auction = new Auction();
        auction.setStartTime(startTime);
        auction.setEndTime(endTime);
        auction.setStatus(status);
        return auction;
    }
}
