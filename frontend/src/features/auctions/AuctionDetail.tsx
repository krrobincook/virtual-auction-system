"use client";

import { useCallback, useEffect, useState } from "react";
import Link from "next/link";
import {
  ArrowLeft,
  ArrowUpRight,
  CalendarDays,
  Clock3,
  Gavel,
  ShieldCheck,
  UserRound,
} from "lucide-react";
import { auctionApi } from "@/api/auctionApi";
import { bidApi } from "@/api/bidApi";
import { getErrorMessage } from "@/api/axiosClient";
import { useAuth } from "@/auth/AuthContext";
import { useRouter } from "next/navigation";
import { useWebSocket } from "@/hooks/useWebSocket";
import { useCountdown } from "@/hooks/useCountdown";
import { useCancelAuction } from "@/hooks/useAuctions";
import { BidForm } from "@/features/bids/BidForm";
import { BidHistory } from "@/features/bids/BidHistory";
import { Button } from "@/components/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/Card";
import { toast } from "@/components/Toast";
import type { Auction, Bid } from "@/types";
import { formatDateTime, formatRupees } from "@/lib/format";

interface AuctionDetailProps {
  auctionId: number;
}

export function AuctionDetail({ auctionId }: AuctionDetailProps) {
  const [auction, setAuction] = useState<Auction | null>(null);
  const [bids, setBids] = useState<Bid[]>([]);
  const [loading, setLoading] = useState(true);
  const { token, isAuthenticated, user } = useAuth();
  const router = useRouter();
  const { cancel, loading: canceling } = useCancelAuction();

  const loadAuction = useCallback(async () => {
    setLoading(true);
    try {
      const [auctionRes, bidsRes] = await Promise.all([
        auctionApi.getById(auctionId),
        bidApi.getHistory(auctionId),
      ]);
      setAuction(auctionRes.data);
      setBids(bidsRes.data);
    } catch {
      toast("Failed to load auction", { icon: "error" });
    } finally {
      setLoading(false);
    }
  }, [auctionId]);

  useEffect(() => {
    const loadId = window.setTimeout(() => loadAuction(), 0);
    return () => window.clearTimeout(loadId);
  }, [loadAuction]);

  const countdownTarget =
    auction?.status === "PENDING" ? auction.startTime : auction?.endTime;
  const { days, hours, minutes, seconds, isEnded } = useCountdown(
    countdownTarget ?? new Date().toISOString()
  );
  const canBid =
    auction !== null &&
    auction.status === "ACTIVE" &&
    !isEnded &&
    isAuthenticated;
  const canManage = auction ? user?.id === auction.sellerId : false;

  const handleCancel = async () => {
    if (!auction || !window.confirm("Cancel this auction? This cannot be undone.")) {
      return;
    }

    try {
      await cancel(auction.id);
      toast("Auction cancelled", { icon: "success" });
      await loadAuction();
    } catch (error) {
      toast(getErrorMessage(error), { icon: "error" });
    }
  };

  useWebSocket(token, auctionId, (message) => {
    if (message.auctionId !== auctionId) return;

    setAuction((current) => {
      if (!current) return current;

      if (message.eventType === "BID_PLACED" && message.currentPrice != null) {
        return { ...current, currentPrice: message.currentPrice };
      }

      if (
        ["AUCTION_ENDED", "AUCTION_CANCELLED"].includes(message.eventType) &&
        message.auctionStatus
      ) {
        return { ...current, status: message.auctionStatus };
      }

      return current;
    });
  });

  if (loading || !auction) {
    return (
      <div className="flex min-h-96 items-center justify-center">
        <div className="text-center">
          <div className="mx-auto size-10 animate-spin rounded-full border-2 border-border border-t-primary" />
          <p className="mt-4 text-sm text-muted-foreground">Opening the lot...</p>
        </div>
      </div>
    );
  }

  const countdownLabel = auction.status === "PENDING" ? "Starts in" : "Ends in";

  return (
    <div className="page-stack">
      <button
        type="button"
        onClick={() => router.back()}
        className="flex w-fit items-center gap-2 text-sm font-semibold text-muted-foreground transition-colors hover:text-foreground"
      >
        <ArrowLeft className="size-4" /> Back to catalogue
      </button>

      <section className="grid gap-6 lg:grid-cols-[1.15fr_0.85fr]">
        <div className="space-y-6">
          <div className="relative flex min-h-[430px] flex-col justify-between overflow-hidden rounded-[1.75rem] bg-secondary p-7 text-secondary-foreground sm:p-10">
            <div className="absolute -right-24 -top-20 size-72 rounded-full border border-accent/20" />
            <div className="absolute bottom-[-25%] left-[20%] size-80 rotate-[-20deg] rounded-[42%] bg-[#f4b59c]" />
            <div className="relative z-10 flex items-start justify-between gap-4">
              <span className="rounded-full bg-accent px-3 py-1 text-[0.65rem] font-bold uppercase tracking-[0.12em] text-accent-foreground">
                {auction.status}
              </span>
              <span className="font-mono text-xs text-secondary-foreground/60">
                LOT {auction.id.toString().padStart(3, "0")}
              </span>
            </div>
            <div className="relative z-10">
              <p className="text-[0.7rem] font-semibold uppercase tracking-[0.16em] text-accent">
                Auction lot {auction.id.toString().padStart(3, "0")}
              </p>
              <h1 className="mt-3 max-w-2xl text-4xl font-semibold leading-[0.94] tracking-[-0.07em] sm:text-6xl">
                {auction.title}
              </h1>
              <p className="mt-5 max-w-lg text-sm leading-relaxed text-secondary-foreground/65">
                This lot is the item being offered for bids. Review its details,
                seller, and bidding window before making your move.
              </p>
            </div>
          </div>

          {canManage && auction.status !== "ENDED" && auction.status !== "CANCELLED" ? (
            <div className="flex flex-wrap items-center gap-3 rounded-2xl border border-border bg-surface p-4">
              <span className="mr-auto text-sm font-semibold">Seller controls</span>
              {auction.status === "PENDING" ? (
                <Link
                  href={`/auctions/${auction.id}/edit`}
                  className="rounded-full border border-border px-4 py-2 text-sm font-semibold transition-colors hover:border-foreground hover:bg-foreground hover:text-background"
                >
                  Edit auction
                </Link>
              ) : null}
              <Button variant="outline" onClick={handleCancel} disabled={canceling}>
                {canceling ? "Cancelling..." : "Cancel auction"}
              </Button>
            </div>
          ) : null}

          <Card>
            <CardHeader className="border-b border-border">
              <div className="flex items-center justify-between">
                <div>
                  <p className="field-label">Lot information</p>
                  <CardTitle className="mt-3 text-2xl">About this item</CardTitle>
                </div>
                <Gavel className="size-6 text-primary" />
              </div>
            </CardHeader>
            <CardContent className="space-y-6 pt-6">
              <p className="leading-relaxed text-muted-foreground">{auction.description}</p>

              <div className="grid gap-4 sm:grid-cols-2">
                <div className="flex items-center gap-3">
                  <span className="grid size-9 place-items-center rounded-full bg-muted">
                    <UserRound className="size-4" />
                  </span>
                  <div>
                    <p className="text-xs text-muted-foreground">Listed by</p>
                    <p className="text-sm font-semibold">{auction.sellerName}</p>
                  </div>
                </div>
                <div className="flex items-center gap-3">
                  <span className="grid size-9 place-items-center rounded-full bg-muted">
                    <ShieldCheck className="size-4" />
                  </span>
                  <div>
                    <p className="text-xs text-muted-foreground">Lot number</p>
                    <p className="text-sm font-semibold">
                      {auction.id.toString().padStart(3, "0")}
                    </p>
                  </div>
                </div>
              </div>

              <div className="grid gap-3 border-t border-border pt-5 sm:grid-cols-2">
                <div className="rounded-xl bg-muted/60 p-4">
                  <p className="text-xs text-muted-foreground">Starting price</p>
                  <p className="mt-1 text-lg font-semibold">
                    {formatRupees(auction.startingPrice)}
                  </p>
                </div>
                <div className="rounded-xl bg-muted/60 p-4">
                  <p className="text-xs text-muted-foreground">Minimum increment</p>
                  <p className="mt-1 text-lg font-semibold">
                    {formatRupees(auction.minimumIncrement ?? "0")}
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        <div className="space-y-6">
          <Card className="overflow-hidden">
            <CardHeader className="border-b border-border">
              <div className="flex items-start justify-between">
                <div>
                  <p className="field-label">Current bid</p>
                  <p className="mt-3 text-5xl font-semibold tracking-[-0.08em]">
                    {formatRupees(auction.currentPrice)}
                  </p>
                </div>
                <ArrowUpRight className="size-6 text-primary" />
              </div>

              <div className="mt-5 rounded-xl bg-muted px-3 py-3 text-sm">
                <div className="flex items-center gap-2">
                  <Clock3 className="size-4 shrink-0 text-primary" />
                  {auction.status === "PENDING" && !isEnded ? (
                    <span>
                      {countdownLabel} <strong>{days}d {hours}h {minutes}m {seconds}s</strong>
                    </span>
                  ) : auction.status === "ACTIVE" && !isEnded ? (
                    <span>
                      {countdownLabel} <strong>{days}d {hours}h {minutes}m {seconds}s</strong>
                    </span>
                  ) : (
                    <span className="text-muted-foreground">
                      Bidding is {auction.status.toLowerCase()}.
                    </span>
                  )}
                </div>
              </div>

              <div className="mt-4 grid gap-3 sm:grid-cols-2">
                <div className="rounded-xl border border-border p-3">
                  <div className="flex items-center gap-2 text-xs font-semibold text-muted-foreground">
                    <CalendarDays className="size-3.5 text-primary" /> Starts
                  </div>
                  <p className="mt-2 text-sm font-semibold">{formatDateTime(auction.startTime)}</p>
                </div>
                <div className="rounded-xl border border-border p-3">
                  <div className="flex items-center gap-2 text-xs font-semibold text-muted-foreground">
                    <CalendarDays className="size-3.5 text-primary" /> Closes
                  </div>
                  <p className="mt-2 text-sm font-semibold">{formatDateTime(auction.endTime)}</p>
                </div>
              </div>
            </CardHeader>

            <CardContent className="pt-6">
              {!isAuthenticated ? (
                <div className="text-center">
                  <p className="text-sm text-muted-foreground">
                    Sign in to place a bid on this auction.
                  </p>
                  <Button onClick={() => router.push("/login")} className="mt-5 w-full">
                    Sign in to bid
                  </Button>
                </div>
              ) : canBid ? (
                <BidForm
                  auctionId={auctionId}
                  currentPrice={auction.currentPrice}
                  minimumIncrement={auction.minimumIncrement}
                  onBidPlaced={loadAuction}
                />
              ) : (
                <div className="py-4 text-center">
                  <p className="text-sm text-muted-foreground">
                    Bidding is unavailable for this auction right now.
                  </p>
                </div>
              )}
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="border-b border-border">
              <div className="flex items-center justify-between">
                <CardTitle className="text-lg">Bid history</CardTitle>
                <span className="font-mono text-xs text-muted-foreground">
                  {bids.length.toString().padStart(2, "0")} bids
                </span>
              </div>
            </CardHeader>
            <CardContent className="pt-4">
              <BidHistory bids={bids} />
            </CardContent>
          </Card>
        </div>
      </section>
    </div>
  );
}
