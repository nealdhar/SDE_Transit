# HW6 - Building a Database for UVA Bus Routes

## Authors
* Olivia Fountain - ogf9uhy
* David Le - uyp7dr
* Neal Dhar - nd2pvz

# Description
Using the UVA DevHub API to get a JSON of bus information for the University Transit
Service, this program translates the JSON information into a list of objects including
bus lines, stops, and routes. These objects are stored in a SQLite database and allows for the 
querying of a functional University Transit Service database, named bus_stops.sqlite3. Tables that include 
the bus stops, bus lines, and routes with all of their respective data fields can be
created in the database, and the DatabaseManager implementation class allows
for function calls of their creation, as well as establishing a JDBC connection, clearing 
data from the tables, deleting the tables, inserting data to the tables, and different
methods of querying the data. 
