import { axiosClient } from "./axiosClient";
import type { Bid, BidRequest } from "@/types";

export const bidApi = {
  place: (auctionId: number, data: BidRequest) =>
    axiosClient.post<Bid>(`/api/auctions/${auctionId}/bids`, data),

  getHistory: (auctionId: number) =>
    axiosClient.get<Bid[]>(`/api/auctions/${auctionId}/bids`),

  getHighest: (auctionId: number) =>
    axiosClient.get<Bid | null>(`/api/auctions/${auctionId}/highest-bid`),
};
