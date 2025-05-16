import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import { getToken } from "next-auth/jwt";

export async function middleware(request: NextRequest) {
  const pathname = request.nextUrl.pathname;

  // Check if the path is a dashboard route
  const isDashboardRoute =
    pathname.startsWith("/(dashboard)") ||
    /^\/[^\/]+\/projects/.test(pathname) ||
    pathname === "/dashboard";

  // Check if the path is an auth route
  const isAuthRoute =
    pathname.startsWith("/login") ||
    pathname.startsWith("/register") ||
    pathname === "/forgot-password";

  // Get the token
  const token = await getToken({
    req: request,
    secret: process.env.NEXTAUTH_SECRET,
  });

  // If it's a dashboard route and no token is found, redirect to login
  if (isDashboardRoute && !token) {
    const url = new URL("/login", request.url);
    url.searchParams.set("callbackUrl", encodeURI(request.url));
    return NextResponse.redirect(url);
  }

  // If it's an auth route and a token is found, redirect to dashboard
  if (isAuthRoute && token) {
    const url = new URL("/dashboard", request.url);
    return NextResponse.redirect(url);
  }

  // Continue for all other routes
  return NextResponse.next();
}

// Configure middleware to run only on specific paths
export const config = {
  matcher: ["/((?!api|_next/static|_next/image|favicon.ico|images|public).*)"],
};
