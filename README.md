# NoCrash Airlines - Flight Ticket System

A comprehensive flight ticket booking and management system developed in Java, implementing all requirements from the Software Project Management Plan (SPMP).

## Project Overview

NoCrash Airlines is a flight ticket booking system that allows passengers to browse, book, and manage flight tickets efficiently through a digital platform. The system also provides airlines with tools to manage flight schedules, seat availability, and booking records.

## Team Information

- **Course**: CPS 731
- **Section**: 011
- **Team**: 19
- **Project Phase**: Implementation (Phase III)

## System Features

### Passenger Features (FR-1 to FR-10)
- ✅ **User Registration** - Create account with personal information
- ✅ **User Authentication** - Secure login with password policy enforcement
- ✅ **Flight Search** - Search flights by origin, destination, and date
- ✅ **Ticket Booking** - Select flights, choose seats, and book tickets
- ✅ **Online Payment** - Secure payment processing with multiple payment methods
- ✅ **Booking Management** - View, modify, or cancel existing bookings
- ✅ **Reschedule Booking** - Change travel dates or select new flights
- ✅ **E-Ticket Generation** - Automatic e-ticket generation and delivery
- ✅ **Notifications** - Email/SMS alerts for bookings, cancellations, and delays
- ✅ **Profile Management** - Update personal details and password

### Admin Features (FR-11 to FR-15)
- ✅ **Admin Authentication** - Secure admin login
- ✅ **Flight Management** - Add, edit, or delete flight details
- ✅ **Seat Inventory Management** - Monitor and adjust seat availability
- ✅ **Booking Oversight** - View and manage all passenger bookings
- ✅ **Report Generation** - Generate sales, passenger trends, and analytics reports

### Airline Staff Features (FR-16 to FR-20)
- ✅ **Passenger List** - View passenger manifest for each flight
- ✅ **Ticket Verification** - Validate boarding passes
- ✅ **Seat Allocation** - Assign and manage seat assignments
- ✅ **Baggage Check-In** - Record baggage information
- ✅ **Operational Alerts** - Send and receive flight alerts

### Database Features (FR-21 to FR-25)
- ✅ **Flight Data Storage** - Store flight schedules and details
- ✅ **Passenger Records** - Securely store passenger information
- ✅ **Transaction Logs** - Maintain audit logs
- ✅ **Data Backup & Recovery** - Automatic backup and recovery support
- ✅ **Real-Time Updates** - Prevent double-booking with real-time updates

### Payment Gateway Features (FR-26 to FR-30)
- ✅ **Secure Payment Processing** - Encrypted payment transactions
- ✅ **Multiple Payment Methods** - Credit/debit cards, digital wallets, online banking
- ✅ **Transaction Verification** - Verify transaction authenticity
- ✅ **Refund Processing** - Secure and trackable refunds
- ✅ **Fraud Detection** - Detect suspicious transactions

## Non-Functional Requirements

- **NFR-1**: System Availability - 24/7 availability with 99.7% uptime
- **NFR-2**: Scalability - Handle 500+ concurrent users
- **NFR-3**: Performance - Booking confirmation within 5 seconds
- **NFR-4**: Data Backup - Automatic daily backups with 2-hour recovery
- **NFR-5**: Error Handling - User-friendly error messages with logging
- **NFR-6**: Security - Strong password policy (min 8 chars, 1 number, 1 special char), account lockout after 5 failed attempts

## Project Structure

```
nocrashairlines/
├── src/main/java/com/nocrashairlines/
│   ├── model/                      # Domain models
│   │   ├── UserAccount.java        # Base user class
│   │   ├── Passenger.java          # Passenger model
│   │   ├── Admin.java              # Admin model
│   │   ├── AirlineStaff.java       # Airline staff model
│   │   ├── Flight.java             # Flight model
│   │   ├── Booking.java            # Booking model
│   │   └── Payment.java            # Payment model
│   ├── service/                    # Business logic services
│   │   ├── AuthenticationService.java  # UC-7, UC-8
│   │   ├── FlightService.java          # UC-1, UC-9
│   │   ├── BookingService.java         # UC-2, UC-4, UC-5, UC-6
│   │   ├── PaymentService.java         # UC-3
│   │   └── AdminService.java           # UC-9, UC-10
│   ├── database/                   # Data persistence
│   │   └── SystemDatabase.java     # In-memory database
│   ├── payment/                    # Payment processing
│   │   ├── PaymentGateway.java     # Payment interface
│   │   ├── PaymentResult.java      # Payment result model
│   │   └── MockPaymentGateway.java # Mock payment implementation
│   ├── util/                       # Utility classes
│   │   ├── PasswordValidator.java  # Password validation
│   │   ├── InputValidator.java     # Input validation
│   │   ├── ValidationResult.java   # Validation result model
│   │   └── NotificationService.java # Email/SMS notifications
│   ├── exception/                  # Custom exceptions
│   │   ├── SystemException.java    # Base exception
│   │   ├── AuthenticationException.java
│   │   ├── BookingException.java
│   │   └── PaymentException.java
│   └── NoCrashAirlinesApp.java    # Main application
├── documentation/                  # Project documentation
│   ├── CPS 731_ flight ticket system_Team 19.txt
│   └── CPS 731_ flight ticket system_Team 19-2.txt
├── pom.xml                        # Maven configuration
└── README.md                      # This file
```

## Use Cases Implemented

1. **UC-1: Search Flights** - Search for available flights by route and date
2. **UC-2: Book Ticket** - Create a flight booking
3. **UC-3: Make Payment** - Process payment for booking
4. **UC-4: Cancel Booking** - Cancel an existing booking
5. **UC-5: Reschedule Flight** - Change booking to a different flight
6. **UC-6: View Booking** - View booking details
7. **UC-7: Passenger Registration** - Register new passenger account
8. **UC-8: Passenger Login** - Authenticate passenger
9. **UC-9: Admin Manage Flights** - Add, edit, delete flights
10. **UC-10: Generate Reports** - Generate various analytics reports

## Technology Stack

- **Language**: Java 17
- **Build Tool**: Maven
- **Testing**: JUnit 5
- **Database**: In-memory (HashMap-based, can be replaced with actual DB)
- **Architecture**: Service-oriented architecture with clear separation of concerns

## Building and Running

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build the Project
```bash
mvn clean compile
```

### Run the Application
```bash
mvn exec:java -Dexec.mainClass="com.nocrashairlines.NoCrashAirlinesApp"
```

Or compile and run directly:
```bash
javac -d target/classes src/main/java/com/nocrashairlines/**/*.java
java -cp target/classes com.nocrashairlines.NoCrashAirlinesApp
```

### Run Tests
```bash
mvn test
```

## Default Credentials

### Admin Account
- **Email**: `admin@nocrashairlines.com`
- **Password**: `Admin@123`

**Note**: The password is case-sensitive and must include the special character `@`. Make sure to type it exactly as shown.

### Sample Flights
The system initializes with 3 sample flights:
- NC101: Toronto → Vancouver
- NC202: Toronto → Montreal
- NC303: Vancouver → Calgary

## Usage Guide

### For Passengers

1. **Register**: Create a new account with your details
2. **Login**: Use your email and password
3. **Search Flights**: Enter origin and destination
4. **Book Ticket**: Select a flight and travel class
5. **Make Payment**: Choose payment method and complete transaction
6. **View Bookings**: Check your booking history
7. **Cancel/Reschedule**: Modify your bookings as needed

### For Admins

1. **Login**: Use admin credentials
2. **Manage Flights**: Add, update, or delete flights
3. **Monitor Bookings**: View all system bookings
4. **Generate Reports**: Access various analytics reports
   - Daily Sales Report
   - Passenger Trends Report
   - Most Booked Routes Report
   - System Statistics Report
   - Flight Occupancy Report

## Security Features

- Password hashing using SHA-256
- Password policy enforcement (NFR-6)
- Account lockout after 5 failed login attempts
- Fraud detection in payment processing
- Transaction logging for audit trails

## Future Enhancements

- Replace in-memory database with PostgreSQL/MySQL
- Implement actual email/SMS service integration
- Add web interface (REST API + React frontend)
- Integrate with real payment gateways (Stripe, PayPal)
- Add seat selection interface
- Implement flight delay notifications
- Add multi-language support
- Implement role-based access control (RBAC)

## License

MIT License - Copyright (c) 2025 Fardin Rahman

## Contributors

- Arooran - Requirement Analysis, System Testing
- Fardin - Requirement Analysis, System Implementation, System Testing
- Nidhi - System Design, System Implementation, System Testing

## Contact

For questions or support, please contact the development team.