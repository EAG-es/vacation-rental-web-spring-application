import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { fine } from "@/lib/fine";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Input } from "@/components/ui/input";
import { useIsMobile } from "@/hooks/use-mobile";
import { Search, User, Menu, X } from "lucide-react";

export function Header() {
  const { data: session } = fine.auth.useSession();
  const navigate = useNavigate();
  const isMobile = useIsMobile();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    navigate(`/properties?search=${encodeURIComponent(searchQuery)}`);
  };

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur">
      <div className="container flex h-16 items-center justify-between px-4 md:px-6">
        <div className="flex items-center gap-2">
          <Link to="/" className="flex items-center">
            <span className="text-xl font-bold text-primary">VacationStay</span>
          </Link>
        </div>

        {!isMobile && (
          <form onSubmit={handleSearch} className="hidden md:flex md:w-1/3">
            <div className="relative w-full">
              <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                type="search"
                placeholder="Search locations..."
                className="w-full pl-8"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>
          </form>
        )}

        <div className="flex items-center gap-4">
          {isMobile ? (
            <Button variant="ghost" size="icon" onClick={toggleMenu}>
              {isMenuOpen ? <X /> : <Menu />}
            </Button>
          ) : (
            <>
              <Button variant="ghost" asChild>
                <Link to="/properties">Browse</Link>
              </Button>
              {session?.user ? (
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button variant="outline" size="icon">
                      <User className="h-4 w-4" />
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end">
                    <DropdownMenuItem asChild>
                      <Link to="/dashboard">Dashboard</Link>
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem asChild>
                      <Link to="/logout">Logout</Link>
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              ) : (
                <Button asChild>
                  <Link to="/login">Sign in</Link>
                </Button>
              )}
            </>
          )}
        </div>
      </div>

      {/* Mobile menu */}
      {isMobile && isMenuOpen && (
        <div className="absolute left-0 right-0 top-16 z-50 border-b bg-background p-4 shadow-lg">
          <form onSubmit={handleSearch} className="mb-4">
            <div className="relative w-full">
              <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                type="search"
                placeholder="Search locations..."
                className="w-full pl-8"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>
          </form>
          <div className="flex flex-col space-y-2">
            <Button variant="ghost" asChild className="justify-start">
              <Link to="/properties" onClick={toggleMenu}>Browse Properties</Link>
            </Button>
            {session?.user ? (
              <>
                <Button variant="ghost" asChild className="justify-start">
                  <Link to="/dashboard" onClick={toggleMenu}>Dashboard</Link>
                </Button>
                <Button variant="ghost" asChild className="justify-start">
                  <Link to="/logout" onClick={toggleMenu}>Logout</Link>
                </Button>
              </>
            ) : (
              <>
                <Button variant="ghost" asChild className="justify-start">
                  <Link to="/login" onClick={toggleMenu}>Sign in</Link>
                </Button>
                <Button variant="ghost" asChild className="justify-start">
                  <Link to="/signup" onClick={toggleMenu}>Sign up</Link>
                </Button>
              </>
            )}
          </div>
        </div>
      )}
    </header>
  );
}