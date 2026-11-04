"use client";

import { useState, useCallback, useEffect } from "react";
import { auctionApi } from "@/api/auctionApi";
import { getErrorMessage } from "@/api/axiosClient";
import type { Auction } from "@/types";

export function useAuctions() {
  const [auctions, setAuctions] = useState<Auction[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await auctionApi.getAll();
      setAuctions(response.data);
    } catch (error) {
      setError(getErrorMessage(error));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const loadId = window.setTimeout(() => load(), 0);
    return () => window.clearTimeout(loadId);
  }, [load]);

  return { auctions, loading, error, reload: load };
}

export function useAuction(id: number | null) {
  const [auction, setAuction] = useState<Auction | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    if (!id) {
      setAuction(null);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const response = await auctionApi.getById(id);
      setAuction(response.data);
    } catch (error) {
      setError(getErrorMessage(error));
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    const loadId = window.setTimeout(() => load(), 0);
    return () => window.clearTimeout(loadId);
  }, [load]);

  return { auction, loading, error, reload: load };
}

export function useCreateAuction() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const create = async (data: Parameters<typeof auctionApi.create>[0]) => {
    setLoading(true);
    setError(null);
    try {
      const response = await auctionApi.create(data);
      return response.data;
    } catch (error) {
      setError(getErrorMessage(error));
      throw error;
    } finally {
      setLoading(false);
    }
  };

  return { create, loading, error };
}

export function useUpdateAuction() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const update = async (id: number, data: Parameters<typeof auctionApi.update>[1]) => {
    setLoading(true);
    setError(null);
    try {
      const response = await auctionApi.update(id, data);
      return response.data;
    } catch (error) {
      setError(getErrorMessage(error));
      throw error;
    } finally {
      setLoading(false);
    }
  };

  return { update, loading, error };
}

export function useCancelAuction() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const cancel = async (id: number) => {
    setLoading(true);
    setError(null);
    try {
      await auctionApi.cancel(id);
    } catch (error) {
      setError(getErrorMessage(error));
      throw error;
    } finally {
      setLoading(false);
    }
  };

  return { cancel, loading, error };
}
