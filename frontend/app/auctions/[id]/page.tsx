"use client";

import { useParams } from "next/navigation";
import { useEffect } from "react";
import { AuctionDetail } from "@/features/auctions/AuctionDetail";

export default function AuctionDetailPage() {
  const params = useParams();
  const auctionId = Number(params.id);

  useEffect(() => {
    if (Number.isNaN(auctionId)) {
      console.error("Invalid auction ID");
    }
  }, [auctionId]);

  if (Number.isNaN(auctionId)) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold">Invalid Auction</h1>
          <p className="text-muted-foreground">
            The auction ID is not valid.
          </p>
        </div>
      </div>
    );
  }

  return <AuctionDetail auctionId={auctionId} />;
}
