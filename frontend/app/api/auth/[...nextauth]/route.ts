import { apiClient } from "@/lib/api/client";
import { User } from "@/types";
import type { NextAuthOptions, Session, User as NextAuthUser } from "next-auth";
import NextAuth from "next-auth";
import CredentialsProvider from "next-auth/providers/credentials";
import { JWT } from "next-auth/jwt";

type AuthResponse = {
  token: string;
  refreshToken: string;
  user: User;
};

// Extended types to properly type the session and JWT
interface ExtendedUser extends NextAuthUser {
  id: string;
  firstName: string;
  lastName: string;
  token: string;
  refreshToken: string;
  avatar?: string;
}

interface ExtendedJWT extends JWT {
  id: string;
  accessToken?: string;
  refreshToken?: string;
  firstName?: string;
  lastName?: string;
  exp?: number;
  error?: string;
}

interface ExtendedSession extends Session {
  user: {
    id: string;
    firstName: string;
    lastName: string;
    accessToken: string;
    error?: string;
    name?: string | null;
    email?: string | null;
    image?: string | null;
  };
}

export const authOptions: NextAuthOptions = {
  providers: [
    CredentialsProvider({
      name: "Credentials",
      credentials: {
        email: { label: "Email", type: "text" },
        password: { label: "Password", type: "password" },
      },
      async authorize(credentials) {
        if (!credentials?.email || !credentials?.password) {
          return null;
        }

        try {
          const response: AuthResponse = await apiClient.post(
            "/auth/login",
            {
              email: credentials.email,
              password: credentials.password,
            },
            false
          ); // false to not require auth for login

          if (response.token && response.user) {
            // Return the user object and include the token in the returned object
            return {
              name: `${response.user.firstName} ${response.user.lastName}`,
              image: response.user.avatar,
              token: response.token,
              refreshToken: response.refreshToken,
              firstName: response.user.firstName,
              lastName: response.user.lastName,
              ...response.user,
            } as ExtendedUser;
          } else {
            return null;
          }
        } catch (error) {
          console.error("Authentication error:", error);
          return null;
        }
      },
    }),
  ],
  callbacks: {
    async jwt({ token, user }): Promise<ExtendedJWT> {
      // Initial sign in
      if (user) {
        const extendedUser = user as ExtendedUser;
        token.id = extendedUser.id;
        token.accessToken = extendedUser.token;
        token.refreshToken = extendedUser.refreshToken;
        token.firstName = extendedUser.firstName;
        token.lastName = extendedUser.lastName;
      }

      // Return previous token if the access token has not expired yet
      const expiryTime = token.exp as number;
      if (expiryTime && Date.now() < expiryTime * 1000 - 60000) {
        return token;
      }

      // Access token has expired, try to update it
      try {
        const refreshResponse = await apiClient.post(
          "/auth/refresh",
          {
            refreshToken: token.refreshToken,
          },
          false
        );

        if (refreshResponse.token) {
          token.accessToken = refreshResponse?.token;
          token.refreshToken = refreshResponse?.refreshToken;
          // Calculate new expiry time
          const jwtPayload = JSON.parse(
            Buffer.from(
              refreshResponse.token.split(".")[1],
              "base64"
            ).toString()
          );
          token.exp = jwtPayload.exp;
        }

        return token;
      } catch (error) {
        console.error("Error refreshing token:", error);
        // If refresh fails, force user to log in again
        return { ...token, error: "RefreshTokenError" };
      }
    },
    async session({ session, token }): Promise<ExtendedSession> {
      const extendedSession = session as ExtendedSession;
      if (token) {
        extendedSession.user.id = token.id as string;
        extendedSession.user.firstName = token.firstName as string;
        extendedSession.user.lastName = token.lastName as string;
        extendedSession.user.accessToken = token.accessToken as string;
        if (token.error) {
          extendedSession.user.error = token.error as string;
        }
      }
      return extendedSession;
    },
  },
  pages: {
    signIn: "/login",
    signOut: "/logout",
    error: "/login", // Error code passed in query string as ?error=
  },
  session: {
    strategy: "jwt",
    maxAge: 30 * 24 * 60 * 60, // 30 days
  },
  secret:
    process.env.NEXTAUTH_SECRET ||
    "hello-world-" + Math.random().toString(36).substring(2, 15),
  debug: process.env.NODE_ENV === "development",
};

const handler = NextAuth(authOptions);

export { handler as GET, handler as POST };
