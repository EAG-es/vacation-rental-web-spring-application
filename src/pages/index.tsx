import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { fine } from "@/lib/fine";
import { Header } from "@/components/layout/Header";
import { PropertyCard } from "@/components/PropertyCard";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useToast } from "@/hooks/use-toast";
import { Search, MapPin, ArrowRight } from "lucide-react";

const Index = () => {
  const [featuredProperties, setFeaturedProperties] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const { toast } = useToast();

  useEffect(() => {
    const fetchFeaturedProperties = async () => {
      try {
        const properties = await fine.table("properties").select().limit(3);
        setFeaturedProperties(properties || []);
      } catch (error) {
        console.error("Error fetching properties:", error);
        toast({
          title: "Error",
          description: "Failed to load featured properties",
          variant: "destructive",
        });
      } finally {
        setIsLoading(false);
      }
    };

    fetchFeaturedProperties();
  }, [toast]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      window.location.href = `/properties?location=${encodeURIComponent(searchQuery)}`;
    }
  };

  const popularDestinations = [
    { name: "Malibu", image: "https://images.unsplash.com/photo-1523906834658-6e24ef2386f9?w=500&auto=format&fit=crop&q=60" },
    { name: "Aspen", image: "https://images.unsplash.com/photo-1605540436563-5bca919ae766?w=500&auto=format&fit=crop&q=60" },
    { name: "New York", image: "https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?w=500&auto=format&fit=crop&q=60" },
  ];

  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      
      {/* Hero Section */}
      <section className="relative h-[500px] w-full">
        <div className="absolute inset-0">
          <img
            src="https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=2070"
            alt="Hero background"
            className="h-full w-full object-cover brightness-75"
          />
        </div>
        <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent" />
        <div className="container relative z-10 mx-auto flex h-full flex-col items-center justify-center px-4 text-center text-white">
          <h1 className="mb-4 text-4xl font-bold md:text-5xl lg:text-6xl">
            Find Your Perfect Vacation Home
          </h1>
          <p className="mb-8 max-w-2xl text-lg md:text-xl">
            Discover and book unique accommodations around the world
          </p>
          
          <form onSubmit={handleSearch} className="w-full max-w-md">
            <div className="relative">
              <MapPin className="absolute left-3 top-3 h-5 w-5 text-muted-foreground" />
              <Input
                type="text"
                placeholder="Where are you going?"
                className="h-12 bg-white pl-10 pr-12 text-black"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
              <Button 
                type="submit" 
                size="icon" 
                className="absolute right-1 top-1 h-10 w-10"
              >
                <Search className="h-5 w-5" />
              </Button>
            </div>
          </form>
        </div>
      </section>

      {/* Featured Properties */}
      <section className="container mx-auto py-12 px-4">
        <div className="mb-8 flex items-center justify-between">
          <h2 className="text-2xl font-bold md:text-3xl">Featured Properties</h2>
          <Button variant="outline" asChild>
            <Link to="/properties" className="flex items-center gap-1">
              View all <ArrowRight className="h-4 w-4" />
            </Link>
          </Button>
        </div>
        
        {isLoading ? (
          <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
            {[1, 2, 3].map((i) => (
              <div key={i} className="h-[350px] rounded-lg bg-muted animate-pulse" />
            ))}
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
            {featuredProperties.map((property) => (
              <PropertyCard key={property.id} property={property} />
            ))}
          </div>
        )}
      </section>

      {/* Popular Destinations */}
      <section className="bg-muted py-12">
        <div className="container mx-auto px-4">
          <h2 className="mb-8 text-2xl font-bold md:text-3xl">Popular Destinations</h2>
          <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
            {popularDestinations.map((destination, index) => (
              <Link 
                key={index} 
                to={`/properties?location=${encodeURIComponent(destination.name)}`}
                className="group relative overflow-hidden rounded-lg"
              >
                <div className="aspect-[4/3]">
                  <img
                    src={destination.image}
                    alt={destination.name}
                    className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-110"
                  />
                </div>
                <div className="absolute inset-0 bg-gradient-to-t from-black/70 to-transparent" />
                <div className="absolute bottom-0 left-0 p-4">
                  <h3 className="text-xl font-bold text-white">{destination.name}</h3>
                </div>
              </Link>
            ))}
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="mt-auto border-t bg-background py-8">
        <div className="container mx-auto px-4">
          <div className="flex flex-col items-center justify-between gap-4 md:flex-row">
            <div>
              <p className="text-lg font-bold">VacationStay</p>
              <p className="text-sm text-muted-foreground">© 2023 VacationStay. All rights reserved.</p>
            </div>
            <div className="flex gap-4">
              <Link to="/properties" className="text-sm hover:underline">Properties</Link>
              <Link to="/about" className="text-sm hover:underline">About</Link>
              <Link to="/contact" className="text-sm hover:underline">Contact</Link>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Index;