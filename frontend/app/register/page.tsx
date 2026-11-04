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

export default function RegisterPage() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await register(name, email, password);
      toast("Registration successful. Please log in.", { icon: "success" });
      router.push("/login");
    } catch (error) {
      toast(getErrorMessage(error), { icon: "error" });
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthFrame eyebrow="Create an account" title="Make your next bid count." description="Create an account to bid on active auctions and publish your own listings." footer={<>Already registered? <Link href="/login" className="font-semibold text-foreground underline decoration-primary underline-offset-4">Sign in instead</Link></>}>
      <form onSubmit={handleSubmit} className="space-y-5">
        <div className="space-y-2"><label className="field-label">Full name</label><Input value={name} onChange={(e) => setName(e.target.value)} placeholder="Your name" required /></div>
        <div className="space-y-2"><label className="field-label">Email address</label><Input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="you@example.com" required /></div>
        <div className="space-y-2"><label className="field-label">Password</label><Input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="At least 8 characters" required /></div>
        <p className="text-xs leading-relaxed text-muted-foreground">By joining, you agree to keep the marketplace thoughtful, honest, and a little bit fun.</p>
        <Button type="submit" disabled={loading} className="w-full" size="lg">{loading ? "Creating account..." : "Create your account"}</Button>
      </form>
    </AuthFrame>
  );
}
