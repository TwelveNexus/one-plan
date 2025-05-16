import Providers from "@/components/providers";
import type { Metadata } from "next";
import { DM_Sans, Inter, JetBrains_Mono } from "next/font/google";
import "./globals.css";

const inter = Inter({
  variable: "--font-oneplan-sans",
  subsets: ["latin"],
  display: "swap",
});

const dmSans = DM_Sans({
  variable: "--font-dm-sans",
  subsets: ["latin"],
  weight: ["400", "500", "600", "700"],
  display: "swap",
});

const jetbrainsMono = JetBrains_Mono({
  variable: "--font-jetbrains-mono",
  subsets: ["latin"],
  display: "swap",
});

export const metadata: Metadata = {
  title: "One Plan - AI-Enhanced Project Management Platform",
  description:
    "Transform your project planning with AI-powered insights, automated storyboarding, and intelligent deadline estimation.",
  keywords: [
    "project management",
    "AI",
    "planning",
    "collaboration",
    "enterprise",
  ],
  authors: [{ name: "Twelve Nexus", url: "https://twelvenexus.com" }],
  openGraph: {
    title: "One Plan - AI-Enhanced Project Management",
    description: "Transform your project planning with AI-powered insights",
    type: "website",
    siteName: "One Plan",
  },
  twitter: {
    card: "summary_large_image",
    title: "One Plan - AI-Enhanced Project Management",
    description: "Transform your project planning with AI-powered insights",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body
        className={`${inter.variable} ${dmSans.variable} ${jetbrainsMono.variable} font-sans antialiased`}
      >
        <Providers>
          <main>{children}</main>
        </Providers>
      </body>
    </html>
  );
}
