package com.auction.virtual_auction_system.service;

import com.auction.virtual_auction_system.controller.dto.BidRequest;
import com.auction.virtual_auction_system.controller.dto.BidResponse;
import com.auction.virtual_auction_system.entity.Auction;
import com.auction.virtual_auction_system.entity.AuctionStatus;
import com.auction.virtual_auction_system.entity.Bid;
import com.auction.virtual_auction_system.entity.User;
import com.auction.virtual_auction_system.exception.InvalidBidException;
import com.auction.virtual_auction_system.repository.AuctionRepository;
import com.auction.virtual_auction_system.repository.BidRepository;
import com.auction.virtual_auction_system.service.Impl.BidServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BidServiceMinimumIncrementTest {

    @Mock
    private BidRepository bidRepository;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private UserService userService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private BidServiceImpl bidService;

    @BeforeEach
    void setUp() {
        bidService = new BidServiceImpl(
                bidRepository,
                auctionRepository,
                userService);
        bidService.setEventPublisher(eventPublisher);
    }

    @Test
    void rejectsBidBelowTheConfiguredMinimumIncrement() {
        User bidder = user("bidder@example.com", "Bidder");
        Auction auction = activeAuction();
        auction.setCurrentPrice(new BigDecimal("100.00"));
        auction.setMinimumIncrement(new BigDecimal("10.00"));

        when(userService.findByEmail("bidder@example.com"))
                .thenReturn(bidder);
        when(auctionRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(auction));

        assertThatThrownBy(() -> bidService.placeBid(
                1L,
                new BidRequest(new BigDecimal("109.99")),
                "bidder@example.com"))
                .isInstanceOf(InvalidBidException.class)
                .hasMessageContaining("110.00");

        verify(bidRepository, never()).save(any(Bid.class));
    }

    @Test
    void acceptsBidAtTheConfiguredMinimumIncrement() {
        User bidder = user("bidder@example.com", "Bidder");
        Auction auction = activeAuction();
        auction.setCurrentPrice(new BigDecimal("100.00"));
        auction.setMinimumIncrement(new BigDecimal("10.00"));

        when(userService.findByEmail("bidder@example.com"))
                .thenReturn(bidder);
        when(auctionRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(auction));
        when(bidRepository.save(any(Bid.class))).thenAnswer(invocation -> {
            Bid bid = invocation.getArgument(0);
            bid.setId(20L);
            return bid;
        });

        BidResponse response = bidService.placeBid(
                1L,
                new BidRequest(new BigDecimal("110.00")),
                "bidder@example.com");

        assertThat(response.getAmount()).isEqualByComparingTo("110.00");
        assertThat(auction.getCurrentPrice()).isEqualByComparingTo("110.00");
        verify(bidRepository).save(any(Bid.class));
    }

    private Auction activeAuction() {
        Auction auction = new Auction();
        auction.setId(1L);
        auction.setStatus(AuctionStatus.ACTIVE);
        auction.setStartTime(LocalDateTime.now().minusMinutes(1));
        auction.setEndTime(LocalDateTime.now().plusMinutes(1));

        User seller = user("seller@example.com", "Seller");
        auction.setSeller(seller);
        return auction;
    }

    private User user(String email, String name) {
        User user = new User();
        user.setId(2L);
        user.setEmail(email);
        user.setName(name);
        return user;
    }
}
