"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useEffect } from "react";
import {
  ArrowUpRight,
  Gavel,
  LayoutDashboard,
  LogOut,
  Plus,
} from "lucide-react";
import { useAuth } from "@/auth/AuthContext";

const navItems = [
  { href: "/dashboard", label: "Overview", icon: LayoutDashboard },
  { href: "/auctions", label: "Auctions", icon: Gavel },
];

export function AppShell({ children }: Readonly<{ children: React.ReactNode }>) {
  const pathname = usePathname();
  const router = useRouter();
  const { user, logout, isAuthenticated, isLoading } = useAuth();
  const isPublic = pathname === "/" || pathname === "/login" || pathname === "/register";

  useEffect(() => {
    if (!isPublic && !isLoading && !isAuthenticated) {
      router.replace("/login");
    }
  }, [isAuthenticated, isLoading, isPublic, router]);

  if (isPublic) return children;
  if (isLoading || !isAuthenticated) return null;

  return (
    <div className="site-background min-h-screen">
      <header className="sticky top-0 z-30 border-b border-border/80 bg-background/90 backdrop-blur-xl">
        <div className="mx-auto flex h-[76px] w-[min(100%-2rem,1240px)] items-center justify-between gap-6">
          <Link href="/" className="group flex items-center gap-3">
            <span className="grid size-10 place-items-center rounded-xl bg-secondary text-accent transition-transform group-hover:rotate-[-8deg]">
              <Gavel className="size-5" strokeWidth={2.4} />
            </span>
            <span className="hidden sm:block">
              <span className="block text-sm font-bold tracking-[-0.04em]">Bid / Found</span>
              <span className="block text-[0.62rem] font-semibold uppercase tracking-[0.18em] text-muted-foreground">
                live auction marketplace
              </span>
            </span>
          </Link>

          <nav className="hidden items-center gap-1 md:flex">
            {navItems.map(({ href, label, icon: Icon }) => {
              const active = pathname === href || pathname.startsWith(`${href}/`);
              return (
                <Link
                  key={href}
                  href={href}
                  className={`flex items-center gap-2 rounded-full px-4 py-2 text-sm font-semibold transition-colors ${
                    active
                      ? "bg-secondary text-secondary-foreground"
                      : "text-muted-foreground hover:bg-muted hover:text-foreground"
                  }`}
                >
                  <Icon className="size-4" />
                  {label}
                </Link>
              );
            })}
          </nav>

          <div className="flex items-center gap-2">
            <Link
              href="/auctions/create"
              className="hidden items-center gap-2 rounded-full bg-accent px-4 py-2 text-sm font-bold text-accent-foreground transition-transform hover:-translate-y-0.5 sm:flex"
            >
              <Plus className="size-4" />
              List an item
            </Link>
            <div className="flex items-center gap-2 rounded-full border border-border bg-surface px-2 py-1.5">
              <span className="grid size-8 place-items-center rounded-full bg-primary text-xs font-bold text-primary-foreground">
                {(user?.email?.[0] ?? "G").toUpperCase()}
              </span>
              <span className="hidden max-w-[140px] truncate text-xs font-semibold sm:block">
                {user?.email ?? "Guest bidder"}
              </span>
              <button
                type="button"
                aria-label="Log out"
                onClick={() => {
                  logout();
                  router.push("/");
                }}
                className="grid size-8 place-items-center rounded-full text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
              >
                <LogOut className="size-4" />
              </button>
            </div>
          </div>
        </div>
      </header>

      <main className="site-main">{children}</main>

      <footer className="border-t border-border/80 bg-surface/70">
        <div className="mx-auto flex w-[min(100%-2rem,1240px)] flex-col gap-3 py-6 text-xs text-muted-foreground sm:flex-row sm:items-center sm:justify-between">
          <span className="font-semibold text-foreground">Bid / Found</span>
          <span className="flex items-center gap-1">
            Live listings. Clear bidding. <ArrowUpRight className="size-3" />
          </span>
        </div>
      </footer>
    </div>
  );
}
