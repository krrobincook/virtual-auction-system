"use client";

import { useState } from "react";
import { auctionApi } from "@/api/auctionApi";
import { getErrorMessage } from "@/api/axiosClient";
import type { Auction, AuctionRequest } from "@/types";
import { Button } from "@/components/Button";
import { Input } from "@/components/Input";
import { toast } from "@/components/Toast";

interface CreateAuctionFormProps {
  auction?: Auction;
  onSuccess: (auction: Awaited<ReturnType<typeof auctionApi.create>>["data"]) => void;
}

const emptyForm: AuctionRequest = {
  title: "",
  description: "",
  startingPrice: "",
  minimumIncrement: "",
  startTime: "",
  endTime: "",
};

function toDatetimeLocal(value: string) {
  return value ? value.slice(0, 16) : "";
}

function getInitialForm(auction?: Auction): AuctionRequest {
  if (!auction) return emptyForm;

  return {
    title: auction.title,
    description: auction.description,
    startingPrice: auction.startingPrice,
    minimumIncrement: auction.minimumIncrement,
    startTime: toDatetimeLocal(auction.startTime),
    endTime: toDatetimeLocal(auction.endTime),
  };
}

export function CreateAuctionForm({ auction, onSuccess }: CreateAuctionFormProps) {
  const [form, setForm] = useState<AuctionRequest>(() => getInitialForm(auction));
  const [loading, setLoading] = useState(false);
  const isEditing = Boolean(auction);

  const update = (field: keyof AuctionRequest, value: string) => {
    setForm((current) => ({ ...current, [field]: value }));
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    const startTime = new Date(form.startTime);
    const endTime = new Date(form.endTime);

    if (startTime <= new Date()) {
      toast("Start time must be in the future", { icon: "error" });
      return;
    }
    if (endTime <= startTime) {
      toast("End time must be after start time", { icon: "error" });
      return;
    }

    setLoading(true);
    try {
      const response = isEditing && auction
        ? await auctionApi.update(auction.id, {
            ...form,
            minimumIncrement: form.minimumIncrement || null,
          })
        : await auctionApi.create({
            ...form,
            minimumIncrement: form.minimumIncrement || null,
          });
      onSuccess(response.data);
      if (!isEditing) setForm(emptyForm);
      toast(isEditing ? "Auction updated successfully" : "Auction created successfully", { icon: "success" });
    } catch (error) {
      toast(getErrorMessage(error), { icon: "error" });
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div className="grid gap-5 md:grid-cols-[1.4fr_0.6fr]">
        <div className="space-y-2"><label className="field-label">Title</label><Input value={form.title} onChange={(event) => update("title", event.target.value)} placeholder="What are you putting into the world?" required /></div>
        <div className="space-y-2"><label className="field-label">Starting price</label><Input type="number" min="0.01" step="0.01" value={form.startingPrice} onChange={(event) => update("startingPrice", event.target.value)} placeholder="0.00" required /></div>
      </div>
      <div className="space-y-2"><label className="field-label">Description</label><textarea value={form.description} onChange={(event) => update("description", event.target.value)} placeholder="Tell bidders what makes this object worth a closer look..." className="min-h-32 w-full resize-y rounded-xl border border-border bg-surface-raised px-4 py-3 text-sm leading-relaxed outline-none placeholder:text-muted-foreground/70 focus:border-primary focus:ring-4 focus:ring-primary/10" required /></div>
      <div className="grid min-w-0 gap-5 sm:grid-cols-2 lg:grid-cols-[minmax(0,0.8fr)_minmax(0,1.1fr)_minmax(0,1.1fr)]">
        <div className="min-w-0 space-y-2"><label htmlFor="minimum-increment" className="field-label">Minimum increment</label><Input id="minimum-increment" type="number" min="0" step="0.01" value={form.minimumIncrement ?? ""} onChange={(event) => update("minimumIncrement", event.target.value)} placeholder="Optional" /></div>
        <div className="min-w-0 space-y-2"><label htmlFor="start-time" className="field-label">Starts</label><Input id="start-time" type="datetime-local" value={form.startTime} onChange={(event) => update("startTime", event.target.value)} className="min-w-0 px-3 text-[0.78rem] sm:text-sm" required /></div>
        <div className="min-w-0 space-y-2"><label htmlFor="end-time" className="field-label">Ends</label><Input id="end-time" type="datetime-local" value={form.endTime} onChange={(event) => update("endTime", event.target.value)} className="min-w-0 px-3 text-[0.78rem] sm:text-sm" required /></div>
      </div>
      <div className="flex flex-col items-start justify-between gap-4 border-t border-border pt-6 sm:flex-row sm:items-center"><p className="max-w-xs text-xs leading-relaxed text-muted-foreground">Listings are reviewed for clarity and safety before they go live.</p><Button type="submit" disabled={loading} size="lg">{loading ? (isEditing ? "Saving changes..." : "Creating your listing...") : (isEditing ? "Save changes" : "Publish auction")}</Button></div>
    </form>
  );
}
