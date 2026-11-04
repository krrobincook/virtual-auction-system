"use client";

import { ArrowUpRight, Gavel, Plus, TrendingUp } from "lucide-react";
import { useAuth } from "@/auth/AuthContext";
import { useAuctions } from "@/hooks/useAuctions";
import { useRouter } from "next/navigation";
import { Button } from "@/components/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/Card";
import { AuctionList } from "@/features/auctions/AuctionList";
import { formatRupees } from "@/lib/format";

export default function DashboardPage() {
  const { isAuthenticated, user } = useAuth();
  const { auctions, loading } = useAuctions();
  const router = useRouter();

  if (!isAuthenticated) return null;

  const activeCount = auctions.filter((auction) => auction.status === "ACTIVE").length;
  const totalValue = auctions.reduce((sum, auction) => sum + Number(auction.currentPrice), 0);

  return (
    <div className="page-stack">
      <section className="relative overflow-hidden rounded-[1.75rem] bg-secondary p-7 text-secondary-foreground sm:p-10"><div className="absolute -right-24 -top-24 size-72 rounded-full border border-accent/20" /><div className="absolute -right-8 -top-8 size-40 rounded-full border border-accent/20" /><div className="relative z-10 flex flex-col justify-between gap-8 md:flex-row md:items-end"><div><p className="eyebrow text-accent">Your auction dashboard</p><h1 className="mt-4 max-w-2xl text-4xl font-semibold tracking-[-0.07em] sm:text-6xl">Welcome back, {user?.name ?? "bidder"}.</h1><p className="mt-4 max-w-lg text-sm leading-relaxed text-secondary-foreground/60">Track current auctions, review active bids, and manage your upcoming listings.</p></div><Button variant="default" onClick={() => router.push("/auctions/create")} className="w-fit bg-accent text-accent-foreground shadow-none hover:bg-accent/90"><Plus className="mr-2 size-4" /> Create auction</Button></div></section>

      <section className="grid gap-4 sm:grid-cols-3"><Card className="p-5"><div className="flex items-start justify-between"><span className="grid size-10 place-items-center rounded-xl bg-accent"><Gavel className="size-5" /></span><ArrowUpRight className="size-4 text-muted-foreground" /></div><p className="mt-8 text-sm text-muted-foreground">Active auctions</p><p className="mt-1 text-3xl font-semibold tracking-[-0.07em]">{activeCount.toString().padStart(2, "0")}</p></Card><Card className="p-5"><div className="flex items-start justify-between"><span className="grid size-10 place-items-center rounded-xl bg-[#f7c3b3]"><TrendingUp className="size-5" /></span><ArrowUpRight className="size-4 text-muted-foreground" /></div><p className="mt-8 text-sm text-muted-foreground">Current auction value</p><p className="mt-1 text-3xl font-semibold tracking-[-0.07em]">{formatRupees(totalValue)}</p></Card><Card className="p-5"><div className="flex items-start justify-between"><span className="grid size-10 place-items-center rounded-xl bg-[#c8d9cf]"><span className="font-mono text-sm">#</span></span><span className="font-mono text-xs text-muted-foreground">ALL STATUS</span></div><p className="mt-8 text-sm text-muted-foreground">Total listings</p><p className="mt-1 text-3xl font-semibold tracking-[-0.07em]">{auctions.length.toString().padStart(2, "0")}</p></Card></section>

      <section><div className="mb-6 flex flex-col justify-between gap-4 sm:flex-row sm:items-end"><div><p className="eyebrow">Auction activity</p><h2 className="mt-3 text-3xl font-semibold tracking-[-0.06em]">Current listings</h2></div><Button variant="ghost" onClick={() => router.push("/auctions")} className="w-fit px-0 text-muted-foreground hover:bg-transparent hover:text-primary">View all auctions <ArrowUpRight className="ml-2 size-4" /></Button></div><Card className="p-4 sm:p-6"><CardHeader className="px-2 pb-6 pt-2"><CardTitle className="text-base font-semibold tracking-[-0.03em]">Latest listings</CardTitle></CardHeader><CardContent className="p-0"><AuctionList auctions={auctions} loading={loading} /></CardContent></Card></section>
    </div>
  );
}
