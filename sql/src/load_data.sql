COPY Users
FROM '/home/csmajs/hobra001/extra/hobra001/project/data/users.csv' 
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE users_userID_seq RESTART 101; 

COPY Hotel
FROM '/home/csmajs/hobra001/extra/hobra001/project/data/hotels.csv'
WITH DELIMITER ',' CSV HEADER;   

COPY Rooms
FROM '/home/csmajs/hobra001/extra/hobra001/project/data/rooms.csv'
WITH DELIMITER ',' CSV HEADER;

COPY MaintenanceCompany
FROM '/home/csmajs/hobra001/extra/hobra001/project/data/company.csv'
WITH DELIMITER ',' CSV HEADER;

COPY RoomBookings
FROM '/home/csmajs/hobra001/extra/hobra001/project/data/bookings.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE RoomBookings_bookingID_seq RESTART 501; 

COPY RoomRepairs
FROM '/home/csmajs/hobra001/extra/hobra001/project/data/roomRepairs.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE roomRepairs_repairID_seq RESTART 11;

COPY RoomRepairRequests
FROM '/home/csmajs/hobra001/extra/hobra001/project/data/roomRepairRequests.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE roomRepairRequests_requestNumber_seq RESTART 11;

COPY RoomUpdatesLog
FROM '/home/csmajs/hobra001/extra/hobra001/project/data/roomUpdatesLog.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE roomUpdatesLog_updateNumber_seq RESTART 51;
