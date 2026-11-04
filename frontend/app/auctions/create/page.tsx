"use client";

import Link from "next/link";
import { ArrowLeft, CircleHelp, Gavel } from "lucide-react";
import { useAuth } from "@/auth/AuthContext";
import { CreateAuctionForm } from "@/features/auctions/CreateAuctionForm";
import { useRouter } from "next/navigation";
import { useEffect } from "react";

export default function CreateAuctionPage() {
  const { isAuthenticated, isLoading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      router.replace("/login");
    }
  }, [isAuthenticated, isLoading, router]);

  if (isLoading || !isAuthenticated) return null;

  const handleSuccess = () => {
    router.push("/auctions");
  };

  return (
    <div className="page-stack mx-auto max-w-5xl"><Link href="/auctions" className="flex w-fit items-center gap-2 text-sm font-semibold text-muted-foreground transition-colors hover:text-foreground"><ArrowLeft className="size-4" /> Back to catalogue</Link><section className="grid gap-10 lg:grid-cols-[0.7fr_1.3fr] lg:items-start"><div><p className="eyebrow">Put something good out there</p><h1 className="mt-4 text-5xl font-semibold tracking-[-0.08em] sm:text-7xl">List an <span className="text-primary">object.</span></h1><p className="mt-6 max-w-sm text-sm leading-relaxed text-muted-foreground">Give your piece the context it deserves. Clear details make for confident bids and happy handoffs.</p><div className="mt-10 hidden rounded-2xl bg-secondary p-6 text-secondary-foreground lg:block"><Gavel className="size-6 text-accent" /><p className="mt-12 text-xl font-semibold tracking-[-0.05em]">The details are the difference.</p><p className="mt-3 text-sm leading-relaxed text-secondary-foreground/60">Strong listings usually include what makes the object unusual, useful, or worth remembering.</p></div></div><div className="rounded-[1.75rem] border border-border bg-surface p-6 shadow-[0_18px_50px_rgba(29,33,30,0.06)] sm:p-8"><div className="mb-8 flex items-start justify-between border-b border-border pb-6"><div><h2 className="text-xl font-semibold tracking-[-0.04em]">Listing details</h2><p className="mt-1 text-sm text-muted-foreground">Make your listing easy to fall for.</p></div><CircleHelp className="size-5 text-muted-foreground" /></div><CreateAuctionForm onSuccess={handleSuccess} /></div></section></div>
  );
}
