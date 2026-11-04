"use client";

import {
  createContext,
  useContext,
  useEffect,
  useState,
  ReactNode,
} from "react";
import type { LoginResponse } from "@/types";
import { authApi } from "@/api/authApi";

type AuthContextType = {
  user: LoginResponse | null;
  token: string | null;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (name: string, email: string, password: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

function getStoredToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem("token");
}

function getStoredUser(): LoginResponse | null {
  if (typeof window === "undefined") return null;
  const stored = localStorage.getItem("user");
  if (!stored) return null;

  try {
    return JSON.parse(stored) as LoginResponse;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<LoginResponse | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const hydrationId = window.setTimeout(() => {
      setUser(getStoredUser());
      setToken(getStoredToken());
      setIsLoading(false);
    }, 0);

    return () => window.clearTimeout(hydrationId);
  }, []);

  const login = async (email: string, password: string) => {
    const response = await authApi.login({ email, password });
    const data = response.data;
    setToken(data.token);
    setUser(data);
    localStorage.setItem("token", data.token);
    localStorage.setItem("user", JSON.stringify(data));
  };

  const register = async (name: string, email: string, password: string) => {
    await authApi.register({ name, email, password });
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  };

  const isAuthenticated = !!token;

  if (isLoading) {
    return null;
  }

  return (
    <AuthContext.Provider
      value={{ user, token, isLoading: false, login, register, logout, isAuthenticated }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
