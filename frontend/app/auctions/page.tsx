"use client";

import { ArrowUpRight, Filter, Plus } from "lucide-react";
import { useAuctions } from "@/hooks/useAuctions";
import { AuctionList } from "@/features/auctions/AuctionList";
import { Button } from "@/components/Button";
import { useRouter } from "next/navigation";

export default function AuctionListPage() {
  const { auctions, loading } = useAuctions();
  const router = useRouter();

  return (
    <div className="page-stack">
      <section className="flex flex-col justify-between gap-8 border-b border-border pb-8 md:flex-row md:items-end"><div><p className="eyebrow">The current catalogue</p><h1 className="mt-4 text-5xl font-semibold tracking-[-0.08em] sm:text-7xl">Find your next <span className="text-primary">favourite.</span></h1><p className="mt-5 max-w-xl text-sm leading-relaxed text-muted-foreground">Open lots from independent sellers, makers, and collectors. Every object has a little more to say.</p></div><Button onClick={() => router.push("/auctions/create")} className="w-fit"><Plus className="mr-2 size-4" /> List an item</Button></section>
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center"><div className="flex items-center gap-2 text-sm text-muted-foreground"><span className="grid size-8 place-items-center rounded-full bg-accent text-foreground"><Filter className="size-4" /></span><span><strong className="text-foreground">{loading ? "—" : auctions.length}</strong> objects in the room</span></div><div className="flex items-center gap-2 text-xs font-semibold text-muted-foreground"><span className="size-2 rounded-full bg-primary" /> Live and upcoming <ArrowUpRight className="size-3" /></div></div>
      <AuctionList auctions={auctions} loading={loading} />
    </div>
  );
}
