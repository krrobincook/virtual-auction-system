package com.auction.virtual_auction_system.service;

import com.auction.virtual_auction_system.controller.dto.AuctionRequest;
import com.auction.virtual_auction_system.controller.dto.AuctionResponse;

import java.util.List;

public interface AuctionService {
    AuctionResponse createAuction(AuctionRequest request, String sellerEmail);
    List<AuctionResponse> getAllAuctions();
    AuctionResponse getAuctionById(Long id);
    AuctionResponse updateAuction(Long id, AuctionRequest request, String sellerEmail);
    void cancelAuction(Long id, String sellerEmail);
}
