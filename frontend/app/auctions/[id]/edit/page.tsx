"use client";

import Link from "next/link";
import { ArrowLeft, CircleHelp } from "lucide-react";
import { useParams, useRouter } from "next/navigation";
import { useAuth } from "@/auth/AuthContext";
import { useAuction } from "@/hooks/useAuctions";
import { CreateAuctionForm } from "@/features/auctions/CreateAuctionForm";

export default function EditAuctionPage() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const { user } = useAuth();
  const auctionId = Number(params.id);
  const { auction, loading } = useAuction(Number.isNaN(auctionId) ? null : auctionId);

  if (Number.isNaN(auctionId)) {
    return <div className="py-20 text-center"><h1 className="text-2xl font-semibold">Invalid auction</h1><p className="mt-2 text-sm text-muted-foreground">The auction ID is not valid.</p></div>;
  }

  if (loading || !auction) {
    return <div className="flex min-h-96 items-center justify-center text-sm text-muted-foreground">Opening auction details...</div>;
  }

  if (user?.id !== auction.sellerId) {
    return <div className="py-20 text-center"><h1 className="text-2xl font-semibold">This lot is not yours to edit.</h1><p className="mt-2 text-sm text-muted-foreground">Only the seller can update an upcoming auction.</p><Link href={`/auctions/${auction.id}`} className="mt-5 inline-flex text-sm font-semibold text-primary">Back to auction</Link></div>;
  }

  if (auction.status !== "PENDING") {
    return <div className="py-20 text-center"><h1 className="text-2xl font-semibold">This auction is already live.</h1><p className="mt-2 text-sm text-muted-foreground">Only upcoming auctions can be updated.</p><Link href={`/auctions/${auction.id}`} className="mt-5 inline-flex text-sm font-semibold text-primary">Back to auction</Link></div>;
  }

  return <div className="page-stack mx-auto max-w-5xl"><Link href={`/auctions/${auction.id}`} className="flex w-fit items-center gap-2 text-sm font-semibold text-muted-foreground transition-colors hover:text-foreground"><ArrowLeft className="size-4" /> Back to auction</Link><section className="grid gap-10 lg:grid-cols-[0.7fr_1.3fr] lg:items-start"><div><p className="eyebrow">Refine your listing</p><h1 className="mt-4 text-5xl font-semibold tracking-[-0.08em] sm:text-7xl">Edit the <span className="text-primary">details.</span></h1><p className="mt-6 max-w-sm text-sm leading-relaxed text-muted-foreground">Keep the story clear and the timing accurate. Changes are only available while the auction is upcoming.</p></div><div className="rounded-[1.75rem] border border-border bg-surface p-6 shadow-[0_18px_50px_rgba(29,33,30,0.06)] sm:p-8"><div className="mb-8 flex items-start justify-between border-b border-border pb-6"><div><h2 className="text-xl font-semibold tracking-[-0.04em]">Listing details</h2><p className="mt-1 text-sm text-muted-foreground">Update {auction.title}.</p></div><CircleHelp className="size-5 text-muted-foreground" /></div><CreateAuctionForm auction={auction} onSuccess={() => router.push(`/auctions/${auction.id}`)} /></div></section></div>;
}
