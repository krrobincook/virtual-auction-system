"use client";

import * as React from "react";
import { cn } from "@/lib/utils";

interface LinkProps extends React.AnchorHTMLAttributes<HTMLAnchorElement> {
  href: string;
}

const Link = React.forwardRef<HTMLAnchorElement, LinkProps>(
  ({ className, href, ...props }, ref) => (
    <a
      ref={ref}
      href={href}
      className={cn("text-primary hover:underline", className)}
      {...props}
    />
  )
);
Link.displayName = "Link";

export { Link };
