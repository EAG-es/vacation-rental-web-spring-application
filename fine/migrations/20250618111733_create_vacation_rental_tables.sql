-- Create properties table
CREATE TABLE properties (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  title TEXT NOT NULL,
  description TEXT NOT NULL,
  location TEXT NOT NULL,
  price REAL NOT NULL,
  bedrooms INTEGER NOT NULL,
  bathrooms INTEGER NOT NULL,
  maxGuests INTEGER NOT NULL,
  amenities TEXT NOT NULL,
  images TEXT NOT NULL,
  ownerId TEXT,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create bookings table
CREATE TABLE bookings (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  propertyId INTEGER NOT NULL,
  userId TEXT NOT NULL,
  startDate TEXT NOT NULL,
  endDate TEXT NOT NULL,
  totalPrice REAL NOT NULL,
  status TEXT NOT NULL DEFAULT 'confirmed',
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (propertyId) REFERENCES properties(id)
);

-- Create reviews table
CREATE TABLE reviews (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  propertyId INTEGER NOT NULL,
  userId TEXT NOT NULL,
  rating INTEGER NOT NULL,
  comment TEXT,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (propertyId) REFERENCES properties(id)
);

-- Insert sample properties
INSERT INTO properties (title, description, location, price, bedrooms, bathrooms, maxGuests, amenities, images)
VALUES 
  ('Beachfront Villa', 'Stunning villa with direct beach access and panoramic ocean views.', 'Malibu, CA', 450, 4, 3, 8, '["WiFi", "Pool", "Beach Access", "Kitchen", "Air Conditioning", "BBQ"]', '["https://images.unsplash.com/photo-1499793983690-e29da59ef1c2", "https://images.unsplash.com/photo-1501876725168-00c445821c9e"]'),
  
  ('Mountain Cabin', 'Cozy cabin nestled in the mountains with breathtaking views.', 'Aspen, CO', 250, 2, 2, 6, '["WiFi", "Fireplace", "Hot Tub", "Kitchen", "Heating", "Parking"]', '["https://images.unsplash.com/photo-1518732714860-b62714ce0c59", "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4"]'),
  
  ('Downtown Loft', 'Modern loft in the heart of the city with easy access to attractions.', 'New York, NY', 300, 1, 1, 2, '["WiFi", "Kitchen", "Air Conditioning", "Gym", "Doorman", "Elevator"]', '["https://images.unsplash.com/photo-1502672260266-1c1ef2d93688", "https://images.unsplash.com/photo-1536376072261-38c75010e6c9"]'),
  
  ('Lakeside Cottage', 'Charming cottage by the lake with private dock and canoe.', 'Lake Tahoe, CA', 200, 3, 2, 6, '["WiFi", "Fireplace", "Lake Access", "Kitchen", "Heating", "Parking"]', '["https://images.unsplash.com/photo-1475113548554-5a36f1f523d6", "https://images.unsplash.com/photo-1470770841072-f978cf4d019e"]'),
  
  ('Tropical Paradise', 'Luxurious villa surrounded by tropical gardens with private pool.', 'Maui, HI', 500, 5, 4, 10, '["WiFi", "Pool", "Beach Access", "Kitchen", "Air Conditioning", "BBQ"]', '["https://images.unsplash.com/photo-1505881502353-a1986add3762", "https://images.unsplash.com/photo-1518684079-3c830dcef090"]'),
  
  ('Desert Oasis', 'Modern home with stunning desert views and private pool.', 'Palm Springs, CA', 350, 3, 2, 6, '["WiFi", "Pool", "Hot Tub", "Kitchen", "Air Conditioning", "BBQ"]', '["https://images.unsplash.com/photo-1512917774080-9991f1c4c750", "https://images.unsplash.com/photo-1523217582562-09d0def993a6"]');