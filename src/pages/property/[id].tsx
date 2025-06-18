import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { fine } from "@/lib/fine";
import { Header } from "@/components/layout/Header";
import { BookingCalendar } from "@/components/BookingCalendar";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from "@/components/ui/dialog";
import { useToast } from "@/hooks/use-toast";
import { Bed, Bath, Users, MapPin, Calendar, Star, CheckCircle } from "lucide-react";

const PropertyDetail = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { toast } = useToast();
  const { data: session } = fine.auth.useSession();
  
  const [property, setProperty] = useState<any>(null);
  const [bookings, setBookings] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [activeImageIndex, setActiveImageIndex] = useState(0);
  const [showBookingDialog, setShowBookingDialog] = useState(false);
  const [bookingDetails, setBookingDetails] = useState<{
    startDate: Date;
    endDate: Date;
    totalPrice: number;
  } | null>(null);
  const [isBookingLoading, setIsBookingLoading] = useState(false);

  useEffect(() => {
    const fetchPropertyAndBookings = async () => {
      if (!id) return;
      
      setIsLoading(true);
      try {
        // Fetch property details
        const propertyResult = await fine.table("properties").select().eq("id", Number(id));
        
        if (!propertyResult || propertyResult.length === 0) {
          navigate("/properties");
          return;
        }
        
        setProperty(propertyResult[0]);
        
        // Fetch existing bookings for this property
        const bookingsResult = await fine.table("bookings").select().eq("propertyId", Number(id));
        setBookings(bookingsResult || []);
      } catch (error) {
        console.error("Error fetching property details:", error);
        toast({
          title: "Error",
          description: "Failed to load property details",
          variant: "destructive",
        });
      } finally {
        setIsLoading(false);
      }
    };

    fetchPropertyAndBookings();
  }, [id, navigate, toast]);

  const handleBookingConfirm = (startDate: Date, endDate: Date, totalPrice: number) => {
    if (!session) {
      toast({
        title: "Authentication required",
        description: "Please sign in to book this property",
      });
      navigate("/login");
      return;
    }
    
    setBookingDetails({ startDate, endDate, totalPrice });
    setShowBookingDialog(true);
  };

  const handleBookingSubmit = async () => {
    if (!bookingDetails || !property || !session) return;
    
    setIsBookingLoading(true);
    try {
      const newBooking = {
        propertyId: property.id,
        userId: session.user.id,
        startDate: bookingDetails.startDate.toISOString().split('T')[0],
        endDate: bookingDetails.endDate.toISOString().split('T')[0],
        totalPrice: bookingDetails.totalPrice,
        status: "confirmed"
      };
      
      await fine.table("bookings").insert(newBooking);
      
      toast({
        title: "Booking confirmed!",
        description: "Your reservation has been successfully processed",
      });
      
      setShowBookingDialog(false);
      navigate("/dashboard");
    } catch (error) {
      console.error("Error creating booking:", error);
      toast({
        title: "Booking failed",
        description: "There was an error processing your booking",
        variant: "destructive",
      });
    } finally {
      setIsBookingLoading(false);
    }
  };

  if (isLoading || !property) {
    return (
      <div className="flex min-h-screen flex-col">
        <Header />
        <div className="container mx-auto flex-1 px-4 py-8">
          <div className="h-[600px] w-full animate-pulse rounded-lg bg-muted" />
        </div>
      </div>
    );
  }

  const images = JSON.parse(property.images) as string[];
  const amenities = JSON.parse(property.amenities) as string[];

  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      
      <main className="container mx-auto flex-1 px-4 py-8">
        <div className="mb-6">
          <h1 className="text-3xl font-bold">{property.title}</h1>
          <div className="mt-2 flex items-center gap-2">
            <MapPin className="h-4 w-4 text-muted-foreground" />
            <span className="text-muted-foreground">{property.location}</span>
            <div className="ml-auto flex items-center">
              <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
              <span className="ml-1">4.8</span>
            </div>
          </div>
        </div>
        
        {/* Image Gallery */}
        <div className="mb-8 grid grid-cols-1 gap-4 md:grid-cols-2">
          <div className="aspect-[4/3] overflow-hidden rounded-lg">
            <img
              src={images[activeImageIndex]}
              alt={property.title}
              className="h-full w-full object-cover"
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            {images.slice(0, 4).map((image, index) => (
              <div 
                key={index} 
                className={`aspect-[4/3] cursor-pointer overflow-hidden rounded-lg ${index === activeImageIndex ? 'ring-2 ring-primary' : ''}`}
                onClick={() => setActiveImageIndex(index)}
              >
                <img
                  src={image}
                  alt={`${property.title} - Image ${index + 1}`}
                  className="h-full w-full object-cover"
                />
              </div>
            ))}
          </div>
        </div>
        
        <div className="grid grid-cols-1 gap-8 lg:grid-cols-3">
          {/* Property Details */}
          <div className="lg:col-span-2">
            <div className="mb-6 flex flex-wrap items-center gap-6 border-b pb-6">
              <div className="flex items-center">
                <Bed className="mr-2 h-5 w-5" />
                <span>{property.bedrooms} Bedrooms</span>
              </div>
              <div className="flex items-center">
                <Bath className="mr-2 h-5 w-5" />
                <span>{property.bathrooms} Bathrooms</span>
              </div>
              <div className="flex items-center">
                <Users className="mr-2 h-5 w-5" />
                <span>Up to {property.maxGuests} guests</span>
              </div>
              <div className="flex items-center">
                <Calendar className="mr-2 h-5 w-5" />
                <span>Instant booking</span>
              </div>
            </div>
            
            <Tabs defaultValue="description">
              <TabsList className="mb-4">
                <TabsTrigger value="description">Description</TabsTrigger>
                <TabsTrigger value="amenities">Amenities</TabsTrigger>
                <TabsTrigger value="location">Location</TabsTrigger>
              </TabsList>
              
              <TabsContent value="description" className="space-y-4">
                <p>{property.description}</p>
                <p>
                  This beautiful property offers a comfortable stay with all the amenities you need for a perfect vacation.
                  Enjoy the stunning views and convenient location close to local attractions.
                </p>
              </TabsContent>
              
              <TabsContent value="amenities">
                <div className="grid grid-cols-2 gap-4 sm:grid-cols-3">
                  {amenities.map((amenity, index) => (
                    <div key={index} className="flex items-center gap-2">
                      <CheckCircle className="h-4 w-4 text-primary" />
                      <span>{amenity}</span>
                    </div>
                  ))}
                </div>
              </TabsContent>
              
              <TabsContent value="location">
                <Card>
                  <CardContent className="p-4">
                    <div className="aspect-[16/9] bg-muted">
                      <div className="flex h-full items-center justify-center">
                        <p className="text-muted-foreground">Map view of {property.location}</p>
                      </div>
                    </div>
                    <p className="mt-4">
                      Located in {property.location}, this property offers easy access to local attractions,
                      restaurants, and entertainment options.
                    </p>
                  </CardContent>
                </Card>
              </TabsContent>
            </Tabs>
          </div>
          
          {/* Booking Calendar */}
          <div>
            <Card className="sticky top-24">
              <CardContent className="p-6">
                <div className="mb-4 flex items-baseline justify-between">
                  <div className="text-2xl font-bold">${property.price}</div>
                  <div className="text-muted-foreground">per night</div>
                </div>
                
                <BookingCalendar 
                  price={property.price} 
                  onBookingConfirm={handleBookingConfirm}
                  existingBookings={bookings}
                />
              </CardContent>
            </Card>
          </div>
        </div>
      </main>
      
      {/* Booking Confirmation Dialog */}
      <Dialog open={showBookingDialog} onOpenChange={setShowBookingDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Confirm your booking</DialogTitle>
            <DialogDescription>
              Please review the details of your booking before confirming.
            </DialogDescription>
          </DialogHeader>
          
          {bookingDetails && (
            <div className="space-y-4">
              <div>
                <h3 className="font-semibold">{property.title}</h3>
                <p className="text-sm text-muted-foreground">{property.location}</p>
              </div>
              
              <div className="space-y-2 rounded-md border p-4">
                <div className="flex justify-between">
                  <span>Check-in</span>
                  <span className="font-medium">{bookingDetails.startDate.toLocaleDateString()}</span>
                </div>
                <div className="flex justify-between">
                  <span>Check-out</span>
                  <span className="font-medium">{bookingDetails.endDate.toLocaleDateString()}</span>
                </div>
                <div className="flex justify-between">
                  <span>Guests</span>
                  <span className="font-medium">2 guests</span>
                </div>
              </div>
              
              <div className="space-y-2 rounded-md border p-4">
                <div className="flex justify-between">
                  <span>Total price</span>
                  <span className="font-bold">${bookingDetails.totalPrice.toFixed(2)}</span>
                </div>
              </div>
            </div>
          )}
          
          <DialogFooter>
            <Button variant="outline" onClick={() => setShowBookingDialog(false)}>
              Cancel
            </Button>
            <Button onClick={handleBookingSubmit} disabled={isBookingLoading}>
              {isBookingLoading ? "Processing..." : "Confirm Booking"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
      
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

export default PropertyDetail;