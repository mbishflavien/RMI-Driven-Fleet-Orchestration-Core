# RMI-Driven Fleet Orchestration Core

A Java RMI-based Vehicle Rental Management System with a Swing GUI client and a multi-service server backend, backed by PostgreSQL via Hibernate ORM.

## Architecture

```
┌──────────────────────────────┐        RMI (port 5000)       ┌──────────────────────────────┐
│  VehicleRentalSystemClient   │ ◄──────────────────────────► │  VehicleRentalSystemServer   │
│  (Swing GUI)                 │                               │  (RMI Registry + Hibernate)  │
└──────────────────────────────┘                               └──────────────────────────────┘
                                                                             │
                                                                             ▼
                                                                    PostgreSQL Database
```

The server exposes six RMI services on port 5000:

| Binding     | Service               |
|-------------|-----------------------|
| `user`      | UserService           |
| `vehicle`   | VehicleService        |
| `rental`    | RentalService         |
| `payment`   | PaymentService        |
| `discount`  | DiscountService       |
| `otp`       | OTPService            |

## Features

- **User management** — registration, login, OTP email verification, profile updates
- **Vehicle management** — add, update, delete, and browse available vehicles
- **Rental management** — create and track rentals
- **Payment processing** — record and manage payments
- **Discount management** — apply and manage discount codes
- **Role-based dashboards** — separate Admin and Customer views
- **Reports & analytics** — exportable reports panel

## Prerequisites

- Java JDK 8 or higher
- NetBeans IDE 8.2+ (recommended) or any IDE supporting Ant builds
- PostgreSQL 12+

## Database Setup

1. Create a PostgreSQL database:
   ```sql
   CREATE DATABASE vehicle_rental_management_system_db;
   ```

2. Update `VehicleRentalSystemServer27857/src/hibernate.cfg.xml` with your credentials:
   ```xml
   <property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/vehicle_rental_management_system_db</property>
   <property name="hibernate.connection.username">your_username</property>
   <property name="hibernate.connection.password">your_password</property>
   ```

   Hibernate is configured with `hbm2ddl.auto=update`, so tables are created automatically on first run.

## Running the Application

### 1. Start the Server

Open `VehicleRentalSystemServer27857` in NetBeans and run `controller.Server`. You should see:

```
Server is running on port 5000...
```

### 2. Start the Client

Open `VehicleRentalSystemClient27857` in NetBeans and run the project. The client connects to `127.0.0.1:5000` by default.

## Project Structure

```
VehicleRentalSystemServer27857/
├── src/
│   ├── controller/        # RMI server entry point
│   ├── dao/               # Hibernate data access objects
│   ├── model/             # JPA entity classes
│   ├── service/           # RMI remote interfaces
│   │   └── implementation/# Service implementations (UnicastRemoteObject)
│   └── util/              # Email (OTP) utilities

VehicleRentalSystemClient27857/
├── src/
│   ├── controller/        # RMI registry lookup
│   ├── model/             # Shared model classes
│   ├── service/           # Remote service interfaces (client-side stubs)
│   ├── util/              # Report exporter
│   └── view/              # Swing GUI frames and panels
│       └── util/          # Reusable UI components (theme, sidebar, table styler)
```

## Author

MBISHIBISHI Flavien
