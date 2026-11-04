import Link from "next/link";
import { ArrowUpRight, Gavel, Sparkles } from "lucide-react";

interface AuthFrameProps {
  eyebrow: string;
  title: string;
  description: string;
  children: React.ReactNode;
  footer: React.ReactNode;
}

export function AuthFrame({ eyebrow, title, description, children, footer }: AuthFrameProps) {
  return (
    <main className="auth-page">
      <aside className="auth-art">
        <Link href="/" className="relative z-10 flex items-center gap-3"><span className="grid size-10 place-items-center rounded-xl bg-accent text-accent-foreground"><Gavel className="size-5" /></span><span><span className="block text-sm font-bold tracking-[-0.04em]">Bid / Found</span><span className="block text-[0.62rem] font-semibold uppercase tracking-[0.18em] text-secondary-foreground/55">live auction marketplace</span></span></Link>
        <div className="relative z-10 max-w-md"><Sparkles className="mb-6 size-7 text-accent" /><p className="text-4xl font-semibold leading-[0.96] tracking-[-0.07em] sm:text-5xl">Good auctions are worth a closer look.</p><p className="mt-6 max-w-sm text-sm leading-relaxed text-secondary-foreground/60">Create an account to bid on active listings or publish your own auction.</p></div>
        <div className="relative z-10 flex items-center justify-between border-t border-secondary-foreground/15 pt-5 text-xs text-secondary-foreground/50"><span>01 / 03</span><span className="flex items-center gap-1">A little more considered <ArrowUpRight className="size-3" /></span></div>
      </aside>
      <section className="auth-content"><div className="mb-8"><p className="eyebrow">{eyebrow}</p><h1 className="mt-4 text-4xl font-semibold tracking-[-0.07em] sm:text-5xl">{title}</h1><p className="mt-4 max-w-md text-sm leading-relaxed text-muted-foreground">{description}</p></div><div className="rounded-[1.5rem] border border-border bg-surface p-6 shadow-[0_20px_60px_rgba(29,33,30,0.07)] sm:p-8">{children}</div><div className="mt-6 text-center text-sm text-muted-foreground">{footer}</div></section>
    </main>
  );
}
