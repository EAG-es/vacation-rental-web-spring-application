import { useState } from "react";
import { Calendar } from "@/components/ui/calendar";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { addDays, differenceInDays, format } from "date-fns";

interface BookingCalendarProps {
  price: number;
  onBookingConfirm: (startDate: Date, endDate: Date, totalPrice: number) => void;
  existingBookings?: { startDate: string; endDate: string }[];
}

export function BookingCalendar({ price, onBookingConfirm, existingBookings = [] }: BookingCalendarProps) {
  const [dateRange, setDateRange] = useState<{
    from: Date | undefined;
    to: Date | undefined;
  }>({
    from: undefined,
    to: undefined,
  });

  // Convert existing bookings to Date objects
  const bookedDates = existingBookings.map(booking => ({
    from: new Date(booking.startDate),
    to: new Date(booking.endDate)
  }));

  // Function to check if a date is disabled
  const isDateDisabled = (date: Date) => {
    // Disable past dates
    if (date < new Date()) return true;
    
    // Check if date falls within any existing booking
    return bookedDates.some(booking => {
      return date >= booking.from && date <= booking.to;
    });
  };

  // Calculate total price based on selected dates
  const calculateTotalPrice = () => {
    if (!dateRange.from || !dateRange.to) return 0;
    const nights = differenceInDays(dateRange.to, dateRange.from);
    return nights * price;
  };

  const totalPrice = calculateTotalPrice();
  const nights = dateRange.from && dateRange.to ? differenceInDays(dateRange.to, dateRange.from) : 0;

  const handleBookNow = () => {
    if (dateRange.from && dateRange.to) {
      onBookingConfirm(dateRange.from, dateRange.to, totalPrice);
    }
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>Select Dates</CardTitle>
      </CardHeader>
      <CardContent>
        <Calendar
          mode="range"
          selected={dateRange}
          onSelect={setDateRange}
          numberOfMonths={1}
          disabled={isDateDisabled}
          className="rounded-md border"
        />
      </CardContent>
      <CardFooter className="flex flex-col">
        <div className="w-full space-y-2">
          {dateRange.from && dateRange.to && (
            <>
              <div className="flex justify-between text-sm">
                <span>
                  {format(dateRange.from, "MMM d, yyyy")} - {format(dateRange.to, "MMM d, yyyy")}
                </span>
                <span>{nights} nights</span>
              </div>
              <div className="flex justify-between font-semibold">
                <span>Total</span>
                <span>${totalPrice.toFixed(2)}</span>
              </div>
            </>
          )}
          <Button 
            onClick={handleBookNow} 
            className="w-full mt-2" 
            disabled={!dateRange.from || !dateRange.to}
          >
            {dateRange.from && dateRange.to ? "Book Now" : "Select Dates"}
          </Button>
        </div>
      </CardFooter>
    </Card>
  );
}