export type Role = "USER" | "ADMIN";

export type AuctionStatus = "PENDING" | "ACTIVE" | "ENDED" | "CANCELLED";

export type AuctionEventType =
  | "BID_PLACED"
  | "AUCTION_STARTED"
  | "AUCTION_ENDED"
  | "AUCTION_CANCELLED";

export interface User {
  id: number;
  name: string;
  email: string;
  role: Role;
  createdAt: string;
}

export interface Auction {
  id: number;
  title: string;
  description: string;
  startingPrice: string;
  currentPrice: string;
  minimumIncrement: string | null;
  startTime: string;
  endTime: string;
  status: AuctionStatus;
  sellerId: number;
  sellerName: string;
  winnerId: number | null;
  winnerName: string | null;
  createdAt: string;
}

export interface Bid {
  id: number;
  auctionId: number;
  bidderId: number;
  bidderName: string;
  amount: string;
  bidTime: string;
}

export interface AuctionUpdateMessage {
  eventType: AuctionEventType;
  auctionId: number;
  currentPrice: string | null;
  minimumIncrement: string | null;
  bidId: number | null;
  bidderId: number | null;
  bidderName: string | null;
  bidAmount: string | null;
  bidTime: string | null;
  auctionStatus: AuctionStatus | null;
  winnerId: number | null;
  winnerName: string | null;
  timestamp: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  id: number;
  name: string;
  email: string;
  token: string;
  role: Role;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface AuctionRequest {
  title: string;
  description: string;
  startingPrice: string;
  minimumIncrement: string | null;
  startTime: string;
  endTime: string;
}

export interface BidRequest {
  amount: string;
}

export interface BidResponse {
  id: number;
  auctionId: number;
  bidderId: number;
  bidderName: string;
  amount: string;
  bidTime: string;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  validationErrors?: Record<string, string>;
}

export interface PaginatedAuctions {
  content: Auction[];
  total: number;
  page: number;
  size: number;
}
