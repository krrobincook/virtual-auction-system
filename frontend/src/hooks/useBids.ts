"use client";

import { useState, useCallback } from "react";
import { useEffect } from "react";
import { bidApi } from "@/api/bidApi";
import { getErrorMessage } from "@/api/axiosClient";
import type { Bid } from "@/types";

export function useBids(auctionId: number | null) {
  const [bids, setBids] = useState<Bid[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    if (!auctionId) return;
    setLoading(true);
    setError(null);
    try {
      const response = await bidApi.getHistory(auctionId);
      setBids(response.data);
    } catch (error) {
      setError(getErrorMessage(error));
    } finally {
      setLoading(false);
    }
  }, [auctionId]);

  useEffect(() => {
    const loadId = window.setTimeout(() => load(), 0);
    return () => window.clearTimeout(loadId);
  }, [load]);

  const place = useCallback(
    async (amount: string) => {
      if (!auctionId) throw new Error("No auction selected");
      setLoading(true);
      setError(null);
      try {
        const response = await bidApi.place(auctionId, { amount });
        return response.data;
      } catch (error) {
        setError(getErrorMessage(error));
        throw error;
      } finally {
        setLoading(false);
      }
    },
    [auctionId]
  );

  return { bids, loading, error, place, reload: load };
}
