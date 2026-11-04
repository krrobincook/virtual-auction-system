"use client";

import { ArrowUpRight, Gavel } from "lucide-react";
import { Auction } from "@/types";
import { AuctionCard } from "./AuctionCard";

interface AuctionListProps {
  auctions: Auction[];
  loading: boolean;
  onCreate?: () => void;
}

export function AuctionList({ auctions, loading }: AuctionListProps) {
  if (loading) {
    return <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">{[1, 2, 3].map((item) => <div key={item} className="animate-pulse overflow-hidden rounded-2xl border border-border bg-surface"><div className="aspect-[1.35] bg-muted" /><div className="space-y-3 p-5"><div className="h-3 w-16 rounded-full bg-muted" /><div className="h-6 w-3/4 rounded-full bg-muted" /><div className="h-3 w-full rounded-full bg-muted" /></div></div>)}</div>;
  }

  if (auctions.length === 0) {
    return <div className="surface-grid flex min-h-72 flex-col items-center justify-center rounded-2xl border border-dashed border-border px-6 text-center"><span className="grid size-14 place-items-center rounded-full bg-accent"><Gavel className="size-6" /></span><h3 className="mt-5 text-xl font-semibold tracking-[-0.04em]">No auctions available.</h3><p className="mt-2 max-w-sm text-sm leading-relaxed text-muted-foreground">There are no listings to show right now. Create an auction or check back later.</p><span className="mt-4 flex items-center gap-1 text-xs font-semibold text-primary">New auctions appear here <ArrowUpRight className="size-3" /></span></div>;
  }

  return <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">{auctions.map((auction) => <AuctionCard key={auction.id} auction={auction} />)}</div>;
}
