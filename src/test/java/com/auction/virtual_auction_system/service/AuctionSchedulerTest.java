package com.auction.virtual_auction_system.service;

import com.auction.virtual_auction_system.entity.Auction;
import com.auction.virtual_auction_system.entity.AuctionStatus;
import com.auction.virtual_auction_system.repository.AuctionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuctionSchedulerTest {

    @Mock
    private AuctionRepository auctionRepository;

    @InjectMocks
    private AuctionScheduler auctionScheduler;

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
