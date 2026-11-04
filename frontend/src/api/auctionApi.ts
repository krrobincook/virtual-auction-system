import { axiosClient } from "./axiosClient";
import type { Auction, AuctionRequest } from "@/types";

export const auctionApi = {
  getAll: () => axiosClient.get<Auction[]>("/api/auctions"),

  getById: (id: number) => axiosClient.get<Auction>(`/api/auctions/${id}`),

  create: (data: AuctionRequest) =>
    axiosClient.post<Auction>("/api/auctions", data),

  update: (id: number, data: AuctionRequest) =>
    axiosClient.put<Auction>(`/api/auctions/${id}`, data),

  cancel: (id: number) =>
    axiosClient.post<void>(`/api/auctions/${id}/cancel`),
};
