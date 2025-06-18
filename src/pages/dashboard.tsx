import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { fine } from "@/lib/fine";
import { Header } from "@/components/layout/Header";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { useToast } from "@/hooks/use-toast";
import { Calendar, Home, User } from "lucide-react";
import { ProtectedRoute } from "@/components/auth/route-components";

const DashboardContent = () => {
  const { data: session } = fine.auth.useSession();
  const [bookings, setBookings] = useState<any[]>([]);
  const [properties, setProperties] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const { toast } = useToast();

  useEffect(() => {
    const fetchUserData = async () => {
      if (!session?.user) return;
      
      setIsLoading(true);
      try {
        // Fetch user's bookings
        const userBookings = await fine.table("bookings").select().eq("userId", session.user.id);
        
        // Fetch property details for each booking
        const bookingsWithDetails = await Promise.all(
          (userBookings || []).map(async (booking) => {
            const propertyDetails = await fine.table("properties").select().eq("id", booking.propertyId);
            return {
              ...booking,
              property: propertyDetails?.[0] || null,
            };
          })
        );
        
        setBookings(bookingsWithDetails);
        
        // Fetch properties owned by user (if any)
        const userProperties = await fine.table("properties").select().eq("ownerId", session.user.id);
        setProperties(userProperties || []);
      } catch (error) {
        console.error("Error fetching user data:", error);
        toast({
          title: "Error",
          description: "Failed to load your dashboard data",
          variant: "destructive",
        });
      } finally {
        setIsLoading(false);
      }
    };

    fetchUserData();
  }, [session, toast]);

  const cancelBooking = async (bookingId: number) => {
    try {
      await fine.table("bookings").update({ status: "cancelled" }).eq("id", bookingId);
      
      // Update local state
      setBookings(bookings.map(booking => 
        booking.id === bookingId 
          ? { ...booking, status: "cancelled" } 
          : booking
      ));
      
      toast({
        title: "Booking cancelled",
        description: "Your booking has been successfully cancelled",
      });
    } catch (error) {
      console.error("Error cancelling booking:", error);
      toast({
        title: "Error",
        description: "Failed to cancel booking",
        variant: "destructive",
      });
    }
  };

  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      
      <main className="container mx-auto flex-1 px-4 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold">Dashboard</h1>
          <p className="text-muted-foreground">
            Welcome back, {session?.user?.name || "Guest"}
          </p>
        </div>
        
        <Tabs defaultValue="bookings">
          <TabsList className="mb-6">
            <TabsTrigger value="bookings" className="flex items-center gap-2">
              <Calendar className="h-4 w-4" />
              My Bookings
            </TabsTrigger>
            <TabsTrigger value="profile" className="flex items-center gap-2">
              <User className="h-4 w-4" />
              Profile
            </TabsTrigger>
          </TabsList>
          
          <TabsContent value="bookings">
            {isLoading ? (
              <div className="space-y-4">
                {[1, 2].map((i) => (
                  <Card key={i} className="animate-pulse">
                    <CardContent className="h-32 p-6" />
                  </Card>
                ))}
              </div>
            ) : bookings.length > 0 ? (
              <div className="space-y-4">
                {bookings.map((booking) => (
                  <Card key={booking.id}>
                    <CardContent className="p-6">
                      <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
                        <div className="md:col-span-1">
                          {booking.property?.images && (
                            <div className="aspect-[4/3] overflow-hidden rounded-md">
                              <img
                                src={JSON.parse(booking.property.images)[0]}
                                alt={booking.property.title}
                                className="h-full w-full object-cover"
                              />
                            </div>
                          )}
                        </div>
                        
                        <div className="md:col-span-2">
                          <h3 className="font-semibold">
                            {booking.property?.title || "Property"}
                          </h3>
                          <p className="text-sm text-muted-foreground">
                            {booking.property?.location || "Location"}
                          </p>
                          
                          <div className="mt-2 flex flex-wrap gap-4 text-sm">
                            <div>
                              <span className="font-medium">Check-in:</span>{" "}
                              {new Date(booking.startDate).toLocaleDateString()}
                            </div>
                            <div>
                              <span className="font-medium">Check-out:</span>{" "}
                              {new Date(booking.endDate).toLocaleDateString()}
                            </div>
                          </div>
                          
                          <div className="mt-2">
                            <span className="text-sm font-medium">Status: </span>
                            <span className={`text-sm ${
                              booking.status === "confirmed" 
                                ? "text-green-600" 
                                : "text-red-600"
                            }`}>
                              {booking.status.charAt(0).toUpperCase() + booking.status.slice(1)}
                            </span>
                          </div>
                        </div>
                        
                        <div className="flex flex-col items-end justify-between md:col-span-1">
                          <div className="font-semibold">
                            ${booking.totalPrice.toFixed(2)}
                          </div>
                          
                          <div className="flex flex-col gap-2">
                            <Button 
                              variant="outline" 
                              size="sm" 
                              asChild
                            >
                              <Link to={`/property/${booking.propertyId}`}>
                                View Property
                              </Link>
                            </Button>
                            
                            {booking.status === "confirmed" && (
                              <Button 
                                variant="destructive" 
                                size="sm"
                                onClick={() => cancelBooking(booking.id)}
                              >
                                Cancel Booking
                              </Button>
                            )}
                          </div>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            ) : (
              <Card>
                <CardContent className="flex flex-col items-center justify-center p-6">
                  <Calendar className="mb-2 h-12 w-12 text-muted-foreground" />
                  <h3 className="text-xl font-semibold">No bookings yet</h3>
                  <p className="text-center text-muted-foreground">
                    You haven't made any bookings yet. Start exploring properties to plan your next vacation!
                  </p>
                  <Button className="mt-4" asChild>
                    <Link to="/properties">Browse Properties</Link>
                  </Button>
                </CardContent>
              </Card>
            )}
          </TabsContent>
          
          <TabsContent value="profile">
            <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
              <Card>
                <CardHeader>
                  <CardTitle>Personal Information</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-2">
                    <div>
                      <span className="font-medium">Name:</span> {session?.user?.name}
                    </div>
                    <div>
                      <span className="font-medium">Email:</span> {session?.user?.email}
                    </div>
                    <div>
                      <span className="font-medium">Member since:</span>{" "}
                      {new Date(session?.user?.createdAt || "").toLocaleDateString()}
                    </div>
                  </div>
                </CardContent>
              </Card>
              
              <Card>
                <CardHeader>
                  <CardTitle>Account Settings</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    <Button variant="outline" className="w-full">
                      Change Password
                    </Button>
                    <Button variant="outline" className="w-full">
                      Edit Profile
                    </Button>
                    <Button variant="outline" className="w-full">
                      Notification Preferences
                    </Button>
                  </div>
                </CardContent>
              </Card>
            </div>
          </TabsContent>
        </Tabs>
      </main>
      
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

const Dashboard = () => {
  return <ProtectedRoute Component={DashboardContent} />;
};

export default Dashboard;