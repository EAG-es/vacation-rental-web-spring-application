import { Link } from "react-router-dom";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Bed, Bath, Users, Star } from "lucide-react";

interface PropertyCardProps {
  property: {
    id: number;
    title: string;
    location: string;
    price: number;
    bedrooms: number;
    bathrooms: number;
    maxGuests: number;
    images: string;
    amenities: string;
  };
}

export function PropertyCard({ property }: PropertyCardProps) {
  // Parse JSON strings
  const images = JSON.parse(property.images) as string[];
  const amenities = JSON.parse(property.amenities) as string[];
  
  // Get first image as main display
  const mainImage = images[0];
  
  // Format price
  const formattedPrice = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(property.price);

  return (
    <Card className="overflow-hidden transition-all hover:shadow-md">
      <Link to={`/property/${property.id}`}>
        <div className="aspect-[4/3] w-full overflow-hidden">
          <img
            src={mainImage}
            alt={property.title}
            className="h-full w-full object-cover transition-transform duration-300 hover:scale-105"
          />
        </div>
        <CardContent className="p-4">
          <div className="flex items-center justify-between">
            <h3 className="font-semibold text-lg truncate">{property.title}</h3>
            <div className="flex items-center">
              <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
              <span className="ml-1 text-sm">4.8</span>
            </div>
          </div>
          <p className="text-muted-foreground text-sm">{property.location}</p>
          
          <div className="mt-2 flex items-center space-x-4 text-sm text-muted-foreground">
            <div className="flex items-center">
              <Bed className="mr-1 h-4 w-4" />
              <span>{property.bedrooms}</span>
            </div>
            <div className="flex items-center">
              <Bath className="mr-1 h-4 w-4" />
              <span>{property.bathrooms}</span>
            </div>
            <div className="flex items-center">
              <Users className="mr-1 h-4 w-4" />
              <span>{property.maxGuests}</span>
            </div>
          </div>
          
          <div className="mt-3 flex flex-wrap gap-1">
            {amenities.slice(0, 3).map((amenity, index) => (
              <Badge key={index} variant="outline" className="text-xs">
                {amenity}
              </Badge>
            ))}
            {amenities.length > 3 && (
              <Badge variant="outline" className="text-xs">
                +{amenities.length - 3} more
              </Badge>
            )}
          </div>
        </CardContent>
        <CardFooter className="border-t p-4 flex justify-between items-center">
          <div>
            <span className="font-semibold">{formattedPrice}</span>
            <span className="text-muted-foreground text-sm"> / night</span>
          </div>
        </CardFooter>
      </Link>
    </Card>
  );
}