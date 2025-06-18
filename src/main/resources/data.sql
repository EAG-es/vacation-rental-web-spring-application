-- Insert sample users
INSERT INTO users (name, email, password, created_at, updated_at) VALUES
('John Doe', 'john@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Jane Smith', 'jane@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert user roles
INSERT INTO user_roles (user_id, role) VALUES
(1, 'USER'),
(2, 'USER');

-- Insert sample properties
INSERT INTO properties (title, description, location, price, bedrooms, bathrooms, max_guests, amenities, images, owner_id, created_at, updated_at) VALUES
('Beachfront Villa', 'Stunning villa with direct beach access and panoramic ocean views.', 'Malibu, CA', 450.00, 4, 3, 8, 
 '["WiFi", "Pool", "Beach Access", "Kitchen", "Air Conditioning", "BBQ"]', 
 '["https://images.unsplash.com/photo-1499793983690-e29da59ef1c2", "https://images.unsplash.com/photo-1501876725168-00c445821c9e"]', 
 '1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
 
('Mountain Cabin', 'Cozy cabin nestled in the mountains with breathtaking views.', 'Aspen, CO', 250.00, 2, 2, 6, 
 '["WiFi", "Fireplace", "Hot Tub", "Kitchen", "Heating", "Parking"]', 
 '["https://images.unsplash.com/photo-1518732714860-b62714ce0c59", "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4"]', 
 '1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
 
('Downtown Loft', 'Modern loft in the heart of the city with easy access to attractions.', 'New York, NY', 300.00, 1, 1, 2, 
 '["WiFi", "Kitchen", "Air Conditioning", "Gym", "Doorman", "Elevator"]', 
 '["https://images.unsplash.com/photo-1502672260266-1c1ef2d93688", "https://images.unsplash.com/photo-1536376072261-38c75010e6c9"]', 
 '2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
 
('Lakeside Cottage', 'Charming cottage by the lake with private dock and canoe.', 'Lake Tahoe, CA', 200.00, 3, 2, 6, 
 '["WiFi", "Fireplace", "Lake Access", "Kitchen", "Heating", "Parking"]', 
 '["https://images.unsplash.com/photo-1475113548554-5a36f1f523d6", "https://images.unsplash.com/photo-1470770841072-f978cf4d019e"]', 
 '2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
 
('Tropical Paradise', 'Luxurious villa surrounded by tropical gardens with private pool.', 'Maui, HI', 500.00, 5, 4, 10, 
 '["WiFi", "Pool", "Beach Access", "Kitchen", "Air Conditioning", "BBQ"]', 
 '["https://images.unsplash.com/photo-1505881502353-a1986add3762", "https://images.unsplash.com/photo-1518684079-3c830dcef090"]', 
 '1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample bookings
INSERT INTO bookings (property_id, user_id, start_date, end_date, total_price, status, created_at, updated_at) VALUES
(1, 2, '2023-12-10', '2023-12-15', 2250.00, 'confirmed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 1, '2023-11-05', '2023-11-10', 1500.00, 'confirmed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, '2023-10-20', '2023-10-25', 1250.00, 'cancelled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample reviews
INSERT INTO reviews (property_id, user_id, rating, comment, created_at) VALUES
(1, 2, 5, 'Amazing property with stunning views! The beach access was perfect and the villa had everything we needed.', CURRENT_TIMESTAMP),
(2, 1, 4, 'Great cabin with beautiful mountain views. The hot tub was a nice touch after a day of hiking.', CURRENT_TIMESTAMP),
(3, 1, 5, 'Perfect location in the heart of NYC. Modern, clean, and comfortable.', CURRENT_TIMESTAMP),
(4, 2, 4, 'Lovely cottage by the lake. Very peaceful and relaxing.', CURRENT_TIMESTAMP);