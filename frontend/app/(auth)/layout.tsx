export default function AuthLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="min-h-screen flex flex-col">
      <div className="flex-1">{children}</div>
      <footer className="border-t py-6 text-center text-sm text-muted-foreground">
        <p>&copy; 2025 Twelve Nexus - One Plan. All rights reserved.</p>
      </footer>
    </div>
  );
}
