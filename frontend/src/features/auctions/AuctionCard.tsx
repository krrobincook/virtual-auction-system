"use client";

import { ArrowUpRight, Clock3, Gavel } from "lucide-react";
import { Auction } from "@/types";
import { Link } from "@/components/Link";
import { useCountdown } from "@/hooks/useCountdown";
import { formatRupees } from "@/lib/format";

interface AuctionCardProps {
  auction: Auction;
}

const statusStyles: Record<string, string> = {
  PENDING: "bg-[#f8df9d] text-[#6d4a12]",
  ACTIVE: "bg-accent text-accent-foreground",
  ENDED: "bg-muted text-muted-foreground",
  CANCELLED: "bg-[#f7c3b3] text-[#7e2f28]",
};

export function AuctionCard({ auction }: AuctionCardProps) {
  const { days, hours, minutes, seconds, isEnded } = useCountdown(auction.endTime);

  return (
    <Link href={`/auctions/${auction.id}`} className="group block h-full">
      <article className="flex h-full flex-col overflow-hidden rounded-2xl border border-border bg-surface transition-all duration-300 hover:-translate-y-1 hover:border-foreground/25 hover:shadow-[0_18px_45px_rgba(29,33,30,0.1)]">
        <div className="relative flex aspect-[1.35] items-end overflow-hidden bg-[#dce5d9] p-5"><div className="absolute -right-12 -top-12 size-44 rounded-full bg-[#f4b59c] transition-transform duration-500 group-hover:scale-125" /><div className="absolute bottom-[-30%] left-[15%] size-48 rounded-[45%] bg-[#aebfca] rotate-[-18deg] transition-transform duration-500 group-hover:rotate-[-28deg]" /><div className="relative flex w-full items-center justify-between"><span className="grid size-10 place-items-center rounded-full bg-surface/85 backdrop-blur"><Gavel className="size-4" /></span><span className={`rounded-full px-3 py-1 text-[0.65rem] font-bold uppercase tracking-[0.12em] ${statusStyles[auction.status] ?? statusStyles.ENDED}`}>{auction.status}</span></div></div>
        <div className="flex flex-1 flex-col p-5"><div className="flex items-start justify-between gap-3"><div><p className="text-[0.65rem] font-semibold uppercase tracking-[0.14em] text-muted-foreground">Lot {auction.id.toString().padStart(3, "0")}</p><h3 className="mt-2 line-clamp-2 text-xl font-semibold leading-tight tracking-[-0.05em]">{auction.title}</h3></div><ArrowUpRight className="size-5 shrink-0 text-muted-foreground transition-transform group-hover:translate-x-1 group-hover:-translate-y-1" /></div><p className="mt-3 line-clamp-2 text-sm leading-relaxed text-muted-foreground">{auction.description}</p><div className="mt-auto pt-6"><div className="flex items-end justify-between border-t border-border pt-4"><div><p className="text-xs text-muted-foreground">Current bid</p><p className="mt-1 text-2xl font-semibold tracking-[-0.06em]">{formatRupees(auction.currentPrice)}</p></div>{auction.status === "ACTIVE" && !isEnded ? <div className="text-right"><div className="flex items-center justify-end gap-1 text-xs text-muted-foreground"><Clock3 className="size-3" /> Ends in</div><p className="mt-1 font-mono text-xs font-semibold">{days}d {hours}h {minutes}m {seconds}s</p></div> : <div className="text-right text-xs text-muted-foreground">{auction.status.toLowerCase()}</div>}</div><div className="mt-4 flex items-center justify-between text-xs text-muted-foreground"><span>by {auction.sellerName}</span><span className="font-semibold text-foreground">View lot</span></div></div></div>
      </article>
    </Link>
  );
}
