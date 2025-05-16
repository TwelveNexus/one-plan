import { DefaultSession } from "next-auth";

declare module "next-auth" {
  /**
   * Extend the built-in session types
   */
  interface Session {
    user: {
      /** User's unique identifier */
      id: string;
      /** User's first name */
      firstName: string;
      /** User's last name */
      lastName: string;
      /** JWT access token */
      accessToken: string;
      /** Error message if any */
      error?: string;
    } & DefaultSession["user"];
  }

  /**
   * Extend the built-in JWT types
   */
  interface JWT {
    /** User's unique identifier */
    id: string;
    /** User's first name */
    firstName?: string;
    /** User's last name */
    lastName?: string;
    /** JWT access token */
    accessToken?: string;
    /** JWT refresh token */
    refreshToken?: string;
    /** Error message if any */
    error?: string;
    /** Token expiration timestamp */
    exp?: number;
  }
}

declare module "next-auth/jwt" {
  /** Extend the built-in JWT types */
  interface JWT {
    /** User's unique identifier */
    id: string;
    /** User's first name */
    firstName?: string;
    /** User's last name */
    lastName?: string;
    /** JWT access token */
    accessToken?: string;
    /** JWT refresh token */
    refreshToken?: string;
    /** Error message if any */
    error?: string;
    /** Token expiration timestamp */
    exp?: number;
  }
}
