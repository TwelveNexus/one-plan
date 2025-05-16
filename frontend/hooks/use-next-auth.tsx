'use client';

import { useSession, signIn, signOut } from 'next-auth/react';
import { useRouter } from 'next/navigation';
import { useCallback, useEffect } from 'react';
import { toast } from 'sonner';
import React from 'react';

export function useNextAuth() {
  const { data: session, status, update } = useSession();
  const router = useRouter();
  const isAuthenticated = status === 'authenticated';
  const isLoading = status === 'loading';

  // Handle login with credentials
  const login = useCallback(async (email: string, password: string) => {
    try {
      const result = await signIn('credentials', {
        email,
        password,
        redirect: false,
      });

      if (!result?.ok) {
        throw new Error(result?.error || 'Login failed');
      }

      // Refresh session data
      await update();

      toast.success('Login successful');
      router.push('/dashboard');
      return true;
    } catch (error) {
      toast.error(error instanceof Error ? error.message : 'Invalid email or password');
      console.error('Login error:', error);
      return false;
    }
  }, [router, update]);

  // Handle logout
  const logout = useCallback(async () => {
    await signOut({ redirect: false });
    toast.success('Logged out successfully');
    router.push('/login');
  }, [router]);

  // Check if token refresh is needed
  useEffect(() => {
    if (session?.user?.error === 'RefreshTokenError') {
      toast.error('Your session has expired. Please login again.');
      logout();
    }
  }, [session, logout]);

  return {
    user: session?.user || null,
    isAuthenticated,
    isLoading,
    login,
    logout,
  };
}

// Fix the type definition for the HOC
export function withAuth<P extends object>(Component: React.ComponentType<P>) {
  return function AuthComponent(props: P) {
    const { isAuthenticated, isLoading } = useNextAuth();
    const router = useRouter();

    useEffect(() => {
      if (!isLoading && !isAuthenticated) {
        router.push('/login');
      }
    }, [isAuthenticated, isLoading, router]);

    if (isLoading) {
      return (
        <div className="flex h-screen items-center justify-center">
          <div className="text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto"></div>
            <p className="mt-2 text-muted-foreground">Loading...</p>
          </div>
        </div>
      );
    }

    if (!isAuthenticated) {
      return null;
    }

    return <Component {...props} />;
  };
}
