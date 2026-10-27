package com.auction.virtual_auction_system.repository;

import com.auction.virtual_auction_system.entity.Auction;
import com.auction.virtual_auction_system.entity.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findBySellerId(Long sellerId);
    List<Auction> findByStatus(AuctionStatus status);
    List<Auction> findAllByOrderByCreatedAtDesc();
}
