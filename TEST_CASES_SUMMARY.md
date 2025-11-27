# NoCrash Airlines - Test Cases Summary

## Overview

This document provides a quick reference summary of all test cases implemented for the NoCrash Airlines Flight Ticket Booking System.

**Total Test Classes**: 7  
**Total Test Methods**: 68  
**Test Success Rate**: 100%  
**Date**: November 26, 2024

---

## Test Classes Summary

### 1. PassengerTest (8 tests)
**File**: `src/test/java/com/nocrashairlines/model/PassengerTest.java`  
**Coverage**: FR-1 (Registration), FR-2 (Login), FR-10 (Profile Management)

1. ✅ `testPassengerCreation()` - Validates passenger object creation with all required fields
2. ✅ `testAddBooking()` - Tests adding booking IDs to passenger's booking list
3. ✅ `testRemoveBooking()` - Tests removing booking IDs from passenger's booking list
4. ✅ `testPreferredClass()` - Tests setting and getting preferred travel class
5. ✅ `testAddress()` - Tests setting and getting passenger address
6. ✅ `testUpdateProfile()` - Tests updating all passenger profile information
7. ✅ `testToString()` - Validates string representation contains key fields
8. ✅ `testMultipleBookings()` - Tests handling multiple bookings correctly

---

### 2. FlightTest (9 tests)
**File**: `src/test/java/com/nocrashairlines/model/FlightTest.java`  
**Coverage**: FR-3 (Flight Search), FR-12 (Flight Management), FR-13 (Seat Inventory)

1. ✅ `testFlightCreation()` - Validates flight object creation with all required fields
2. ✅ `testReserveSeat()` - Tests successful seat reservation and seat count decrement
3. ✅ `testReserveSeatWhenFull()` - Tests seat reservation failure when flight is full
4. ✅ `testReleaseSeat()` - Tests releasing a reserved seat and seat count increment
5. ✅ `testReleaseSeatBeyondCapacity()` - Tests that seats don't exceed total capacity
6. ✅ `testClassPrices()` - Tests setting and getting prices for different travel classes
7. ✅ `testFlightStatus()` - Tests updating flight status (SCHEDULED, DELAYED, DEPARTED, ARRIVED)
8. ✅ `testFlightEquality()` - Tests flight equality based on flight ID and number
9. ✅ `testToString()` - Validates string representation contains flight details

---

### 3. BookingTest (9 tests)
**File**: `src/test/java/com/nocrashairlines/model/BookingTest.java`  
**Coverage**: FR-4 (Ticket Booking), FR-6 (Booking Management), FR-7 (Reschedule)

1. ✅ `testBookingCreation()` - Validates booking creation with PENDING status
2. ✅ `testConfirmBooking()` - Tests confirming a booking and status change to CONFIRMED
3. ✅ `testCancelBooking()` - Tests cancelling a booking and status change to CANCELLED
4. ✅ `testCanBeCancelled()` - Tests business logic for cancellable bookings
5. ✅ `testRescheduleBooking()` - Tests rescheduling to a different flight
6. ✅ `testCanBeRescheduled()` - Tests business logic for reschedulable bookings
7. ✅ `testPaymentId()` - Tests setting and getting payment ID
8. ✅ `testCheckIn()` - Tests check-in process and baggage tag assignment
9. ✅ `testToString()` - Validates string representation contains booking details

---

### 4. PaymentTest (10 tests)
**File**: `src/test/java/com/nocrashairlines/model/PaymentTest.java`  
**Coverage**: FR-5 (Payment), NFR-1 to NFR-5 (Payment Security, Methods, Verification, Refund, Fraud)

1. ✅ `testPaymentCreation()` - Validates payment creation with PENDING status
2. ✅ `testMarkAsSuccess()` - Tests marking payment as successful with transaction reference
3. ✅ `testMarkAsFailed()` - Tests marking payment as failed
4. ✅ `testProcessRefund()` - Tests processing refund with reason and date
5. ✅ `testCanBeRefunded()` - Tests business logic for refundable payments
6. ✅ `testFraudDetection()` - Tests fraud detection flag functionality
7. ✅ `testPaymentMethods()` - Tests support for multiple payment methods (Credit, Debit, Wallet, Banking)
8. ✅ `testCardInformation()` - Tests secure storage of card information (last 4 digits only)
9. ✅ `testToString()` - Validates string representation contains payment details
10. ✅ `testPaymentLifecycle()` - Tests complete payment lifecycle (PENDING → SUCCESS → REFUNDED)

---

### 5. AdminTest (7 tests)
**File**: `src/test/java/com/nocrashairlines/model/AdminTest.java`  
**Coverage**: FR-11 (Admin Authentication)

1. ✅ `testAdminCreation()` - Validates admin object creation with all required fields
2. ✅ `testAdminLevel()` - Tests setting and getting admin level (SUPER_ADMIN, ADMIN, MODERATOR)
3. ✅ `testDepartment()` - Tests setting and getting admin department
4. ✅ `testUpdateAdminProfile()` - Tests updating admin profile information
5. ✅ `testUserAccountInheritance()` - Validates inheritance from UserAccount base class
6. ✅ `testToString()` - Validates string representation contains admin details
7. ✅ `testDifferentAdminLevels()` - Tests support for different admin privilege levels

---

### 6. PasswordValidatorTest (11 tests)
**File**: `src/test/java/com/nocrashairlines/util/PasswordValidatorTest.java`  
**Coverage**: NFR-6 (Password Policy and Security)

1. ✅ `testValidPassword()` - Tests acceptance of valid password with all requirements
2. ✅ `testNullPassword()` - Tests rejection of null password
3. ✅ `testEmptyPassword()` - Tests rejection of empty password
4. ✅ `testShortPassword()` - Tests rejection of password shorter than 8 characters
5. ✅ `testPasswordWithoutNumbers()` - Tests rejection of password without numbers
6. ✅ `testPasswordWithoutSpecialChars()` - Tests rejection of password without special characters
7. ✅ `testPasswordWithDifferentSpecialChars()` - Tests acceptance of various special characters
8. ✅ `testStrongPassword()` - Tests identification of strong passwords (12+ chars, mixed case, numbers, special)
9. ✅ `testWeakPassword()` - Tests rejection of weak passwords as not strong
10. ✅ `testMinimumLength()` - Tests minimum length requirement enforcement
11. ✅ `testEdgeCases()` - Tests edge cases (spaces-only, very long passwords)

---

### 7. InputValidatorTest (14 tests)
**File**: `src/test/java/com/nocrashairlines/util/InputValidatorTest.java`  
**Coverage**: All FRs (Data Validation)

1. ✅ `testValidEmails()` - Tests acceptance of valid email formats
2. ✅ `testInvalidEmails()` - Tests rejection of invalid email formats
3. ✅ `testNullOrEmptyEmail()` - Tests rejection of null or empty email
4. ✅ `testValidPhoneNumbers()` - Tests acceptance of valid phone numbers (E.164 format)
5. ✅ `testFormattedPhoneNumbers()` - Tests acceptance of formatted phone numbers (spaces, dashes)
6. ✅ `testInvalidPhoneNumbers()` - Tests rejection of invalid phone numbers
7. ✅ `testValidPassportNumbers()` - Tests acceptance of valid passport numbers (6-9 alphanumeric)
8. ✅ `testInvalidPassportNumbers()` - Tests rejection of invalid passport numbers
9. ✅ `testValidNames()` - Tests acceptance of valid names (alphabetic with spaces, hyphens, apostrophes)
10. ✅ `testInvalidNames()` - Tests rejection of invalid names (numbers, special characters)
11. ✅ `testValidAmounts()` - Tests acceptance of valid payment amounts (positive, within limits)
12. ✅ `testInvalidAmounts()` - Tests rejection of invalid amounts (zero, negative, excessive)
13. ✅ `testValidSeatNumbers()` - Tests acceptance of valid seat numbers (e.g., "12A", "5B")
14. ✅ `testInvalidSeatNumbers()` - Tests rejection of invalid seat numbers

---

## Test Execution Results

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.nocrashairlines.util.PasswordValidatorTest
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.nocrashairlines.util.InputValidatorTest
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.nocrashairlines.model.AdminTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.nocrashairlines.model.FlightTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.nocrashairlines.model.BookingTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.nocrashairlines.model.PaymentTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.nocrashairlines.model.PassengerTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] Tests run: 68, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## How to Run Tests

### Run All New Tests
```bash
mvn test -Dtest=PassengerTest,FlightTest,BookingTest,PaymentTest,AdminTest,PasswordValidatorTest,InputValidatorTest
```

### Run Individual Test Class
```bash
mvn test -Dtest=PassengerTest
mvn test -Dtest=FlightTest
mvn test -Dtest=BookingTest
mvn test -Dtest=PaymentTest
mvn test -Dtest=AdminTest
mvn test -Dtest=PasswordValidatorTest
mvn test -Dtest=InputValidatorTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=PassengerTest#testAddBooking
mvn test -Dtest=FlightTest#testReserveSeat
mvn test -Dtest=PaymentTest#testProcessRefund
```

---

## Test Coverage by Functional Requirement

| Requirement | Test Classes | Status |
|-------------|--------------|--------|
| FR-1: Passenger Registration | PassengerTest, AuthenticationServiceTest | ✅ |
| FR-2: Passenger Login | PassengerTest, AuthenticationServiceTest | ✅ |
| FR-3: Flight Search | FlightTest, FlightServiceTest | ✅ |
| FR-4: Ticket Booking | BookingTest, BookingServiceTest | ✅ |
| FR-5: Online Payment | PaymentTest, PaymentServiceTest | ✅ |
| FR-6: Booking Management | BookingTest, BookingServiceTest | ✅ |
| FR-7: Reschedule Booking | BookingTest, BookingServiceTest | ✅ |
| FR-10: Profile Management | PassengerTest, AuthenticationServiceTest | ✅ |
| FR-11: Admin Authentication | AdminTest, AuthenticationServiceTest | ✅ |
| FR-12: Flight Management | FlightTest, AdminServiceTest | ✅ |
| FR-13: Seat Inventory | FlightTest, FlightServiceTest | ✅ |
| NFR-1 to NFR-5: Payment Features | PaymentTest, PaymentServiceTest | ✅ |
| NFR-6: Password Policy | PasswordValidatorTest | ✅ |
| All FRs: Data Validation | InputValidatorTest | ✅ |

---

**Document Version**: 1.0  
**Last Updated**: November 26, 2024  
**Status**: All Tests Passing ✅

