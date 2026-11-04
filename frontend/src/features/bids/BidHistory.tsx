"use client";

import { Bid } from "@/types";
import { formatRupees } from "@/lib/format";

interface BidHistoryProps { bids: Bid[]; }

export function BidHistory({ bids }: BidHistoryProps) {
  if (bids.length === 0) return <div className="surface-grid rounded-xl border border-dashed border-border px-5 py-10 text-center"><p className="text-sm font-semibold">No bids yet.</p><p className="mt-2 text-xs text-muted-foreground">Be the first person to make a move.</p></div>;

  return <div className="space-y-1">{bids.map((bid, index) => <div key={bid.id} className="flex items-center justify-between rounded-xl px-3 py-3 transition-colors hover:bg-muted/60"><div className="flex items-center gap-3"><span className={`grid size-9 place-items-center rounded-full text-xs font-bold ${index === 0 ? "bg-accent text-foreground" : "bg-muted text-muted-foreground"}`}>{(bid.bidderName?.[0] ?? "B").toUpperCase()}</span><div><p className="text-sm font-semibold">{bid.bidderName}</p><p className="text-xs text-muted-foreground">{new Date(bid.bidTime).toLocaleString()}</p></div></div><span className="font-mono text-sm font-semibold">{formatRupees(bid.amount)}</span></div>)}</div>;
}
