package com.auction.virtual_auction_system.service.Impl;

import com.auction.virtual_auction_system.controller.dto.BidRequest;
import com.auction.virtual_auction_system.controller.dto.BidResponse;
import com.auction.virtual_auction_system.entity.Auction;
import com.auction.virtual_auction_system.entity.AuctionStatus;
import com.auction.virtual_auction_system.entity.Bid;
import com.auction.virtual_auction_system.entity.User;
import com.auction.virtual_auction_system.exception.AuctionNotActiveException;
import com.auction.virtual_auction_system.exception.InvalidBidException;
import com.auction.virtual_auction_system.exception.ResourceNotFoundException;
import com.auction.virtual_auction_system.repository.AuctionRepository;
import com.auction.virtual_auction_system.repository.BidRepository;
import com.auction.virtual_auction_system.service.BidService;
import com.auction.virtual_auction_system.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final UserService userService;

    public BidServiceImpl(
            BidRepository bidRepository,
            AuctionRepository auctionRepository,
            UserService userService) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
        this.userService = userService;
    }

    @Transactional
    @Override
    public BidResponse placeBid(
            Long auctionId,
            BidRequest request,
            String bidderEmail) {

        if (request == null || request.getAmount() == null
                || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidBidException("Bid amount must be greater than zero");
        }

        // The bidder identity comes from the verified JWT, never from JSON.
        User bidder = userService.findByEmail(bidderEmail);

        /*
         * The pessimistic write lock makes the read/validate/write sequence
         * atomic at the database level, including when multiple app instances
         * receive bids at the same time.
         */
        Auction auction = auctionRepository.findByIdForUpdate(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Auction not found with id: " + auctionId));

        if (auction.getSeller() != null
                && bidder.getEmail().equalsIgnoreCase(
                auction.getSeller().getEmail())) {
            // Sellers must not increase the price of their own auction.
            throw new InvalidBidException(
                    "Seller cannot bid on their own auction");
        }

        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            // PENDING is this project's equivalent of an upcoming auction.
            throw new AuctionNotActiveException(
                    "Auction is not active. Current status: "
                            + auction.getStatus());
        }

        LocalDateTime now = LocalDateTime.now();
        // Status alone is not sufficient; enforce the actual auction window.
        if (auction.getStartTime() == null || now.isBefore(auction.getStartTime())) {
            throw new AuctionNotActiveException(
                    "Auction has not started yet");
        }
        if (auction.getEndTime() == null || !now.isBefore(auction.getEndTime())) {
            throw new AuctionNotActiveException(
                    "Auction has ended");
        }

        BigDecimal currentPrice = auction.getCurrentPrice();
        if (currentPrice == null) {
            throw new InvalidBidException(
                    "Auction current price is not available");
        }
        if (request.getAmount().compareTo(currentPrice) <= 0) {
            throw new InvalidBidException(
                    "Bid amount must be greater than the current price of "
                            + currentPrice);
        }

        Bid bid = new Bid();
        bid.setAmount(request.getAmount());
        bid.setBidTime(now);
        bid.setBidder(bidder);
        bid.setAuction(auction);

        Bid savedBid = bidRepository.save(bid);

        // Keep the denormalized auction price synchronized with this bid.
        auction.setCurrentPrice(savedBid.getAmount());
        auctionRepository.save(auction);
        // Earlier Bid rows remain immutable bidding history.

        return mapToResponse(savedBid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BidResponse> getBiddingHistory(Long auctionId) {
        ensureAuctionExists(auctionId);
        return bidRepository.findByAuctionIdOrderByBidTimeDesc(auctionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BidResponse> getHighestBid(Long auctionId) {
        ensureAuctionExists(auctionId);
        return bidRepository.findTopByAuctionIdOrderByAmountDesc(auctionId)
                .map(this::mapToResponse);
    }

    private void ensureAuctionExists(Long auctionId) {
        if (auctionId == null || !auctionRepository.existsById(auctionId)) {
            throw new ResourceNotFoundException(
                    "Auction not found with id: " + auctionId);
        }
    }

    private BidResponse mapToResponse(Bid bid) {
        return new BidResponse(
                bid.getId(),
                bid.getAuction().getId(),
                bid.getBidder().getId(),
                bid.getBidder().getName(),
                bid.getAmount(),
                bid.getBidTime());
    }
}
