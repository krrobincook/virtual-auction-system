import { toast as sonnerToast } from "react-hot-toast";

export function toast(message: string, options?: { duration?: number; icon?: "success" | "error" | "loading" }) {
  sonnerToast(message, {
    duration: options?.duration ?? 4000,
  });
}

export function useToast() {
  return {
    toast: (message: string, options?: Parameters<typeof toast>[1]) => {
      toast(message, options);
    },
  };
}
