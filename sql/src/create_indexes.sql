
CREATE INDEX index_hotelDistance
ON Hotel (latitude, longitude);

CREATE INDEX index_bookingDate
ON RoomBookings (hotelID, roomNumber)
WHERE bookingDate IS NULL;

CREATE INDEX index_viewBookings
ON RoomBookings USING BTREE
(customerID, bookingDate DESC);

CREATE INDEX index_roomNumber
ON Rooms (hotelID, roomNumber);

CREATE INDEX index_roomBookings
ON RoomBookings (hotelID, customerID);

CREATE INDEX index_roomRepairs
ON RoomRepairs (hotelID, roomNumber);




