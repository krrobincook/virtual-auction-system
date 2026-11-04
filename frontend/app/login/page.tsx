"use client";

import Link from "next/link";
import { useState } from "react";
import { useAuth } from "@/auth/AuthContext";
import { useRouter } from "next/navigation";
import { Button } from "@/components/Button";
import { Input } from "@/components/Input";
import { AuthFrame } from "@/components/AuthFrame";
import { toast } from "@/components/Toast";
import { getErrorMessage } from "@/api/axiosClient";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await login(email, password);
      toast("Logged in successfully", { icon: "success" });
      router.push("/dashboard");
    } catch (error) {
      toast(getErrorMessage(error), { icon: "error" });
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthFrame eyebrow="Welcome back" title="Pick up where you left off." description="Sign in to follow your watchlist, place a bid, or list something with a story of its own." footer={<>New here? <Link href="/register" className="font-semibold text-foreground underline decoration-primary underline-offset-4">Create an account</Link></>}>
      <form onSubmit={handleSubmit} className="space-y-5">
        <div className="space-y-2"><label className="field-label">Email address</label><Input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="you@example.com" required /></div>
        <div className="space-y-2"><div className="flex items-center justify-between"><label className="field-label">Password</label><span className="text-xs text-muted-foreground">Keep it private</span></div><Input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="••••••••" required /></div>
        <Button type="submit" disabled={loading} className="mt-2 w-full" size="lg">{loading ? "Signing in..." : "Sign in to Bid / Found"}</Button>
      </form>
    </AuthFrame>
  );
}
