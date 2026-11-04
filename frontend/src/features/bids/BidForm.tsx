"use client";

import { useState } from "react";
import { ArrowUpRight, LockKeyhole } from "lucide-react";
import { bidApi } from "@/api/bidApi";
import { getErrorMessage } from "@/api/axiosClient";
import { Button } from "@/components/Button";
import { Input } from "@/components/Input";
import { toast } from "@/components/Toast";
import { formatRupees } from "@/lib/format";

interface BidFormProps { auctionId: number; currentPrice: string; minimumIncrement: string | null; onBidPlaced: () => void; }

export function BidForm({ auctionId, currentPrice, minimumIncrement, onBidPlaced }: BidFormProps) {
  const [amount, setAmount] = useState("");
  const [loading, setLoading] = useState(false);
  const minBid = (parseFloat(currentPrice) + parseFloat(minimumIncrement || "0")).toFixed(2);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try { await bidApi.place(auctionId, { amount }); setAmount(""); onBidPlaced(); toast("Bid placed successfully", { icon: "success" }); } catch (error) { toast(getErrorMessage(error), { icon: "error" }); } finally { setLoading(false); }
  };

  return <form onSubmit={handleSubmit} className="space-y-5"><div className="flex items-center justify-between"><div><p className="field-label">Your next move</p><p className="mt-2 text-sm text-muted-foreground">Minimum bid is {formatRupees(minBid)}</p></div><span className="grid size-10 place-items-center rounded-full bg-accent"><ArrowUpRight className="size-5" /></span></div><div className="relative"><span className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2 text-xl font-semibold">₹</span><Input type="number" step="0.01" min={minBid} value={amount} onChange={(e) => setAmount(e.target.value)} placeholder={minBid} className="h-16 pl-10 text-2xl font-semibold tracking-[-0.05em]" required /></div><Button type="submit" disabled={loading} className="w-full" size="lg">{loading ? "Placing bid..." : "Place your bid"}</Button><p className="flex items-center justify-center gap-1 text-center text-xs text-muted-foreground"><LockKeyhole className="size-3" /> Your bid is securely submitted.</p></form>;
}
