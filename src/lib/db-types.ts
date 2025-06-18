export type Schema = {
  properties: {
    id?: number;
    title: string;
    description: string;
    location: string;
    price: number;
    bedrooms: number;
    bathrooms: number;
    maxGuests: number;
    amenities: string; // JSON string of amenities array
    images: string; // JSON string of image URLs array
    ownerId?: string | null;
    createdAt?: string;
    updatedAt?: string;
  };
  
  bookings: {
    id?: number;
    propertyId: number;
    userId: string;
    startDate: string;
    endDate: string;
    totalPrice: number;
    status?: string;
    createdAt?: string;
    updatedAt?: string;
  };
  
  reviews: {
    id?: number;
    propertyId: number;
    userId: string;
    rating: number;
    comment?: string | null;
    createdAt?: string;
  };
}