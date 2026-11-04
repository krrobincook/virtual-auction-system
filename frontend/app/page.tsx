import Link from "next/link";
import {
  ArrowDownRight,
  ArrowUpRight,
  Clock3,
  Gavel,
  ListChecks,
  LogIn,
  ShieldCheck,
  Sparkles,
} from "lucide-react";
import { Button } from "@/components/Button";

export const instant = false;

const auctionFeatures = [
  { icon: Gavel, number: "01", title: "Browse live auctions", text: "See current lots, prices, sellers, and closing times in one place." },
  { icon: Clock3, number: "02", title: "Bid with clarity", text: "Know the minimum bid and follow bidding history as it changes." },
  { icon: ListChecks, number: "03", title: "Manage your listings", text: "Create, update, and cancel your own upcoming auctions." },
];

export default function HomePage() {
  return (
    <main className="min-h-screen overflow-hidden bg-background">
      <header className="relative z-10 mx-auto flex h-[76px] w-[min(100%-2rem,1240px)] items-center justify-between">
        <Link href="/" className="group flex items-center gap-3">
          <span className="grid size-10 place-items-center rounded-xl bg-secondary text-accent transition-transform group-hover:rotate-[-8deg]"><Gavel className="size-5" strokeWidth={2.4} /></span>
          <span><span className="block text-sm font-bold tracking-[-0.04em]">Bid / Found</span><span className="block text-[0.62rem] font-semibold uppercase tracking-[0.18em] text-muted-foreground">live auction marketplace</span></span>
        </Link>
        <nav className="hidden items-center gap-6 text-sm font-semibold text-muted-foreground md:flex"><a href="#how-it-works" className="transition-colors hover:text-foreground">How it works</a><a href="#auction-tools" className="transition-colors hover:text-foreground">Auction tools</a></nav>
        <Link href="/register" className="rounded-full bg-secondary px-4 py-2 text-sm font-semibold text-secondary-foreground transition-transform hover:-translate-y-0.5">Create account <ArrowUpRight className="ml-1 inline size-4" /></Link>
      </header>

      <section className="site-background relative border-y border-border/80">
        <div className="mx-auto grid min-h-[650px] w-[min(100%-2rem,1240px)] items-center gap-12 py-16 lg:grid-cols-[1.05fr_0.95fr] lg:py-20">
          <div className="relative z-10">
            <h1 className="display-title max-w-4xl text-foreground">See what&apos;s up for auction.</h1>
            <p className="mt-8 max-w-xl text-lg leading-relaxed text-muted-foreground">Browse active lots, place competitive bids, and put your own items up for auction with clear pricing and timing.</p>
            <div className="mt-9 flex flex-wrap items-center gap-3"><Link href="/auctions"><Button size="lg">Browse auctions <ArrowUpRight className="ml-2 size-4" /></Button></Link><Link href="/login"><Button size="lg" variant="outline"> Log in</Button></Link></div>
          </div>

          <div className="relative mx-auto w-full max-w-[540px]"><div className="surface-grid relative overflow-hidden rounded-[2rem] border border-border p-5 shadow-[0_30px_80px_rgba(29,33,30,0.15)]"><div className="absolute -right-16 -top-20 size-64 rounded-full bg-accent/70 blur-3xl" /><div className="absolute -bottom-20 -left-20 size-72 rounded-full bg-primary/25 blur-3xl" /><div className="relative rounded-[1.5rem] bg-secondary p-6 text-secondary-foreground sm:p-8"><div className="flex items-center justify-between border-b border-secondary-foreground/15 pb-5"><div><p className="text-[0.65rem] font-semibold uppercase tracking-[0.18em] text-accent">Auction workflow</p><p className="mt-2 text-sm text-secondary-foreground/60">Everything you need to participate.</p></div><Gavel className="size-6 text-accent" /></div><div className="space-y-3 py-8">{auctionFeatures.map(({ icon: Icon, number, title }) => <div key={number} className="flex items-center gap-4 rounded-2xl border border-secondary-foreground/15 px-4 py-4"><span className="grid size-10 place-items-center rounded-full bg-accent text-accent-foreground"><Icon className="size-4" /></span><span className="flex-1"><span className="block text-xs font-semibold uppercase tracking-[0.12em] text-secondary-foreground/50">Step {number}</span><span className="mt-1 block font-semibold">{title}</span></span><ArrowUpRight className="size-4 text-accent" /></div>)}</div><div className="flex items-center gap-3 border-t border-secondary-foreground/15 pt-5 text-xs text-secondary-foreground/60"><ShieldCheck className="size-4 text-accent" /> Authenticated accounts and protected auction actions.</div></div></div></div>
        </div>
      </section>

      <section id="how-it-works" className="mx-auto w-[min(100%-2rem,1240px)] py-24"><div className="flex flex-col justify-between gap-6 md:flex-row md:items-end"><div><p className="eyebrow">How it works</p><h2 className="section-title mt-4 max-w-xl">The auction details stay front and centre.</h2></div><p className="max-w-sm text-sm leading-relaxed text-muted-foreground">No filler dashboards or hidden steps—just the information needed to make a good auction decision.</p></div><div className="mt-12 grid gap-px overflow-hidden rounded-2xl border border-border bg-border md:grid-cols-3">{auctionFeatures.map(({ icon: Icon, number, title, text }) => <div key={number} className="bg-surface p-7 transition-colors hover:bg-accent/30"><div className="flex items-center justify-between"><Icon className="size-6 text-primary" /><span className="font-mono text-xs text-muted-foreground">{number}</span></div><h3 className="mt-16 text-xl font-semibold tracking-[-0.04em]">{title}</h3><p className="mt-3 text-sm leading-relaxed text-muted-foreground">{text}</p></div>)}</div></section>

      <section id="auction-tools" className="bg-secondary text-secondary-foreground"><div className="mx-auto grid w-[min(100%-2rem,1240px)] gap-10 py-20 md:grid-cols-[1fr_1.2fr] md:items-end"><div><p className="eyebrow text-accent">Built for auctions</p><h2 className="section-title mt-4 max-w-lg">Clear listings. Live bids. Fewer surprises.</h2></div><div className="grid gap-4 sm:grid-cols-2"><div className="border-t border-secondary-foreground/20 pt-4"><p className="text-3xl font-semibold tracking-[-0.06em]">Live status</p><p className="mt-2 text-sm leading-relaxed text-secondary-foreground/60">Track pending, active, ended, and cancelled auctions as they move through their lifecycle.</p></div><div className="border-t border-secondary-foreground/20 pt-4"><p className="text-3xl font-semibold tracking-[-0.06em]">Bid history</p><p className="mt-2 text-sm leading-relaxed text-secondary-foreground/60">Review every bid and see the current price update in real time.</p></div></div></div></section>

      <section className="mx-auto flex w-[min(100%-2rem,1240px)] flex-col gap-6 py-16 sm:flex-row sm:items-center sm:justify-between"><div><p className="eyebrow">Start bidding</p><h2 className="mt-3 text-3xl font-semibold tracking-[-0.06em]">Find an auction and make your move.</h2></div><Link href="/auctions"><Button variant="secondary" size="lg">View auctions <ArrowDownRight className="ml-2 size-4" /></Button></Link></section>
    </main>
  );
}
