"use client";

import { useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import { API_BASE_URL } from "@/api/axiosClient";
import type { AuctionUpdateMessage } from "@/types";

type OnMessageHandler = (message: AuctionUpdateMessage) => void;

export function useWebSocket(
  token: string | null,
  auctionId: number | null,
  onMessage: OnMessageHandler | null
) {
  const handlerRef = useRef(onMessage);

  useEffect(() => {
    handlerRef.current = onMessage;
  }, [onMessage]);

  useEffect(() => {
    if (!token || !auctionId) return;

    const client = new Client({
      brokerURL:
        process.env.NEXT_PUBLIC_WS_URL ||
        `${API_BASE_URL.replace(/^http/, "ws")}/ws`,
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      onConnect: () => {
        client.subscribe(`/topic/auctions/${auctionId}`, (message) => {
          try {
            const payload = JSON.parse(message.body) as AuctionUpdateMessage;
            if (handlerRef.current) {
              handlerRef.current(payload);
            }
          } catch {
            // ignore malformed messages
          }
        });
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.activate();

    return () => {
      client.deactivate();
    };
  }, [token, auctionId]);

  return { send: null };
}
