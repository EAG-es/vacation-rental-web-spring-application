import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { fine } from "@/lib/fine";
import { Header } from "@/components/layout/Header";
import { PropertyCard } from "@/components/PropertyCard";
import { SearchFilters } from "@/components/SearchFilters";
import { useToast } from "@/hooks/use-toast";

const Properties = () => {
  const [properties, setProperties] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchParams] = useSearchParams();
  const { toast } = useToast();

  useEffect(() => {
    const fetchProperties = async () => {
      setIsLoading(true);
      try {
        let query = fine.table("properties").select();
        
        // Apply filters from URL params
        const location = searchParams.get("location");
        const minPrice = searchParams.get("minPrice");
        const maxPrice = searchParams.get("maxPrice");
        const bedrooms = searchParams.get("bedrooms");
        const bathrooms = searchParams.get("bathrooms");
        const guests = searchParams.get("guests");
        
        if (location) {
          query = query.like("location", `%${location}%`);
        }
        
        if (minPrice) {
          query = query.gt("price", Number(minPrice));
        }
        
        if (maxPrice) {
          query = query.lt("price", Number(maxPrice));
        }
        
        if (bedrooms) {
          query = query.gt("bedrooms", Number(bedrooms) - 1);
        }
        
        if (bathrooms) {
          query = query.gt("bathrooms", Number(bathrooms) - 1);
        }
        
        if (guests) {
          query = query.gt("maxGuests", Number(guests) - 1);
        }
        
        const results = await query;
        setProperties(results || []);
      } catch (error) {
        console.error("Error fetching properties:", error);
        toast({
          title: "Error",
          description: "Failed to load properties",
          variant: "destructive",
        });
      } finally {
        setIsLoading(false);
      }
    };

    fetchProperties();
  }, [searchParams, toast]);

  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      
      <main className="container mx-auto flex-1 px-4 py-8">
        <SearchFilters />
        
        {isLoading ? (
          <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {[1, 2, 3, 4, 5, 6].map((i) => (
              <div key={i} className="h-[350px] rounded-lg bg-muted animate-pulse" />
            ))}
          </div>
        ) : properties.length > 0 ? (
          <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {properties.map((property) => (
              <PropertyCard key={property.id} property={property} />
            ))}
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center py-12 text-center">
            <h3 className="mb-2 text-xl font-semibold">No properties found</h3>
            <p className="text-muted-foreground">
              Try adjusting your search filters to find more options.
            </p>
          </div>
        )}
      </main>
      
      <footer className="mt-auto border-t bg-background py-8">
        <div className="container mx-auto px-4">
          <div className="flex flex-col items-center justify-between gap-4 md:flex-row">
            <div>
              <p className="text-lg font-bold">VacationStay</p>
              <p className="text-sm text-muted-foreground">© 2023 VacationStay. All rights reserved.</p>
            </div>
            <div className="flex gap-4">
              <a href="#" className="text-sm hover:underline">Properties</a>
              <a href="#" className="text-sm hover:underline">About</a>
              <a href="#" className="text-sm hover:underline">Contact</a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Properties;