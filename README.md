# SnowScape# Ski Center Management System

## Overview
The **Ski Center Management System** is a Java-based application that integrates a PostgreSQL database to manage ski resorts, accommodations, and user interactions. The system allows users to browse ski centers, check accommodations, leave reviews, and manage their accounts.

This project includes:
- A **relational database** storing ski centers, accommodations, users, and reviews.
- A **Java backend** that interacts with the database via JDBC.
- A **user interface** (not included in this repository) that provides a graphical representation of the system.

## Features
### **1. Ski Center Management**
- Retrieve all ski centers from the database.
- Filter ski centers by name, city, county, or region.
- Store details such as **location, contact information, and operating hours**.

### **2. Accommodation Management**
- Fetch accommodations associated with a ski center.
- Store details like **name, category, distance from the ski center, website, and address**.
- Designed to support **hotels, lodges, and other stay options**.

### **3. User Account Management**
- Register a new user with a unique username and password.
- Authenticate users upon login.
- Prevent duplicate usernames.
- Allow changing the username.
- Enable deletion of user accounts.

### **4. Review System**
- Users can leave **general reviews** or reviews specific to **ski centers** and **accommodations**.
- Reviews include **ratings** for different aspects (e.g., slope conditions, room comfort, staff service).
- Users can **view reviews left by others**.
- Allows deletion of reviews.

## Installation & Setup
### **Prerequisites**
- Java Development Kit (**JDK 8+**)
- PostgreSQL Database Server
- JDBC Driver for PostgreSQL
- An IDE like IntelliJ IDEA, Eclipse, or NetBeans


### **Running the Application**
1. Open the project in your IDE.
2. Update the database credentials.
3. Compile and run the Java files.
4. Ensure PostgreSQL is running before launching the program.


## Future Enhancements
- **Enhanced Security:** Implement password hashing.
- **User Roles:** Differentiate between regular users and admins.
- **Search Filters:** Advanced filtering for ski centers.
- **Real-Time Updates:** Use WebSockets to update reviews dynamically.
- **Mobile Support:** Adapt the UI for mobile usage.
