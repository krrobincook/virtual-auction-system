"use client";

import { AuthProvider } from "@/auth/AuthContext";
import { AppShell } from "@/components/AppShell";
import { Toaster } from "react-hot-toast";

export function ClientLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  return (
    <AuthProvider>
      <Toaster position="top-right" />
      <AppShell>{children}</AppShell>
    </AuthProvider>
  );
}
