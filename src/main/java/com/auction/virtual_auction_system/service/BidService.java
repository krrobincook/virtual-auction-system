package com.auction.virtual_auction_system.service;

import com.auction.virtual_auction_system.controller.dto.BidRequest;
import com.auction.virtual_auction_system.controller.dto.BidResponse;

import java.util.List;
import java.util.Optional;

public interface BidService {

    BidResponse placeBid(Long auctionId, BidRequest request, String bidderEmail);

    List<BidResponse> getBiddingHistory(Long auctionId);

    Optional<BidResponse> getHighestBid(Long auctionId);
}
