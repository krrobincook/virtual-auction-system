package com.auction.virtual_auction_system.repository;

import com.auction.virtual_auction_system.entity.Auction;
import com.auction.virtual_auction_system.entity.AuctionStatus;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findBySellerId(Long sellerId);
    List<Auction> findByStatus(AuctionStatus status);
    List<Auction> findByStatusAndStartTimeLessThanEqual(
            AuctionStatus status,
            LocalDateTime time);
    List<Auction> findByStatusAndEndTimeLessThanEqual(
            AuctionStatus status,
            LocalDateTime time);
    List<Auction> findAllByOrderByCreatedAtDesc();

    /**
     * Locks the auction row in the database while a bid is validated and saved.
     * This protects concurrent requests across multiple application instances.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Auction a where a.id = :id")
    Optional<Auction> findByIdForUpdate(@Param("id") Long id);
}
