DROP TABLE IF EXISTS Hotel CASCADE;
DROP TABLE IF EXISTS Rooms CASCADE;
DROP TABLE IF EXISTS Users CASCADE;
DROP TABLE IF EXISTS MaintenanceCompany CASCADE;
DROP TABLE IF EXISTS RoomBookings CASCADE;
DROP TABLE IF EXISTS RoomRepairs CASCADE;
DROP TABLE IF EXISTS RoomRepairRequests CASCADE;
DROP TABLE IF EXISTS RoomUpdatesLog CASCADE;

CREATE TABLE Users ( userID serial,
                     name char(50),
                     password char(11) NOT NULL,
                     userType char(10),  ---userType can be 'customer' or 'manager' 
                     PRIMARY KEY(userID)
);
--Note: 'serial' is a special datatype of Postgres. The value of the field having serial datatype
--is autoincremented if any new row is inserted.

CREATE TABLE Hotel ( hotelID integer, 
                     hotelName char(30) NOT NULL,
                     latitude decimal(8,6),
                     longitude decimal(9,6),
                     dateEstablished date,
                     managerUserID integer NOT NULL,
		             PRIMARY KEY(hotelID), 
                     FOREIGN KEY(managerUserID) REFERENCES Users(userID)
);

CREATE TABLE Rooms ( hotelID integer NOT NULL, 
                    roomNumber integer NOT NULL,
                    price integer NOT NULL,
                    imageURL char(30),
                    PRIMARY KEY(hotelID, roomNumber), 
                    FOREIGN KEY(hotelID) REFERENCES Hotel(hotelID) ON DELETE CASCADE
);

CREATE TABLE MaintenanceCompany ( 
                        companyID integer,
                        name char(50),
                        addrress Char(50),
                        PRIMARY KEY(companyID)
);


CREATE TABLE RoomBookings ( 
                    bookingID serial NOT NULL,
                    customerID integer NOT NULL,
                    hotelID integer NOT NULL,
                    roomNumber integer NOT NULL, 
                    bookingDate date NOT NULL, 
                    PRIMARY KEY(bookingID),
                    FOREIGN KEY(customerID) REFERENCES Users(userID),
                    FOREIGN KEY(hotelID, roomNumber) REFERENCES Rooms(hotelID, roomNumber)
);

CREATE TABLE RoomRepairs (  
                            repairID serial,
                            companyID integer NOT NULL,
                            hotelID integer NOT NULL,
                            roomNumber integer NOT NULL, 
                            repairDate date NOT NULL,
                            PRIMARY KEY(repairID),
                            FOREIGN KEY(companyID) REFERENCES MaintenanceCompany(companyID),
                            FOREIGN KEY(hotelID, roomNumber) REFERENCES Rooms(hotelID, roomNumber)
);


CREATE TABLE RoomRepairRequests ( 
                               requestNumber serial,
                               managerID integer NOT NULL, 
               	               repairID integer NOT NULL, 
                               PRIMARY KEY(requestNumber), 
                               FOREIGN KEY(managerID) REFERENCES Users(userID), 
			                   FOREIGN KEY(repairID) REFERENCES RoomRepairs(repairID)
);

--The following table stores the information about any update of the room's information done by any Manager.
CREATE TABLE RoomUpdatesLog (
                            updateNumber serial,
                            managerID integer NOT NULL,
                            hotelID integer NOT NULL,
                            roomNumber integer NOT NULL,
                            updatedOn timestamp NOT NULL,
                            PRIMARY KEY(updateNumber),
                            FOREIGN KEY(managerID) REFERENCES Users(userID),
                            FOREIGN KEY(hotelID, roomNumber) REFERENCES Rooms(hotelID, roomNumber)
);

---The following is the definition of a user-defined sql function for calculating the distance between two lat-long pairs.
CREATE OR REPLACE FUNCTION calculate_distance(lat1 decimal, long1 decimal, lat2 decimal, long2 decimal)
RETURNS decimal AS $dist$
BEGIN RETURN sqrt((lat1 - lat2) * (lat1 - lat2) + (long1 - long2) * (long1 - long2));
END;
$dist$ LANGUAGE plpgsql;
