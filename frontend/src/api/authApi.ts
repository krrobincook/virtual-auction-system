import { axiosClient } from "./axiosClient";
import type { LoginRequest, LoginResponse, RegisterRequest } from "@/types";

export const authApi = {
  register: (data: RegisterRequest) =>
    axiosClient.post<string>("/api/auth/register", data),

  login: (data: LoginRequest) =>
    axiosClient.post<LoginResponse>("/api/auth/login", data),
};
