package com.nocrashairlines.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.nocrashairlines.database.SystemDatabase.TransactionLog;
import com.nocrashairlines.model.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class SystemDatabase {
    private static SystemDatabase instance;
    public static Connection connect() throws Exception {
    String url = "POSTGRESURL";
    String user = "postgres";
    String password = "PASSWORD";

    return DriverManager.getConnection(url, user, password);

}

public static synchronized SystemDatabase getInstance() {
    if (instance == null) {
        instance = new SystemDatabase();
    }
    return instance;
}


public boolean savePassenger(Passenger passenger) {
    if (passenger == null || passenger.getUserId() == null) return false;

    String insertUserSql = """
        INSERT INTO users (user_id, name, email, password_hash, phone_number)
        VALUES (?, ?, ?, ?, ?)
        ON CONFLICT (user_id) DO UPDATE SET
            name = EXCLUDED.name,
            email = EXCLUDED.email,
            password_hash = EXCLUDED.password_hash,
            phone_number = EXCLUDED.phone_number
    """;

    String insertPassengerSql = """
        INSERT INTO passengers (user_id, passport_number, address, preferred_class)
        VALUES (?, ?, ?, ?)
        ON CONFLICT (user_id) DO UPDATE SET
            passport_number = EXCLUDED.passport_number,
            address = EXCLUDED.address,
            preferred_class = EXCLUDED.preferred_class
    """;

    try (Connection conn = SystemDatabase.connect()) {

        // 1) Insert/update the user row
        try (var stmt = conn.prepareStatement(insertUserSql)) {
            stmt.setString(1, passenger.getUserId());
            stmt.setString(2, passenger.getName());
            stmt.setString(3, passenger.getEmail());
            stmt.setString(4, passenger.getPassword());
            stmt.setString(5, passenger.getPhoneNumber());
            stmt.executeUpdate();
        }

        // 2) Insert/update the passengers row
        try (var stmt = conn.prepareStatement(insertPassengerSql)) {
            stmt.setString(1, passenger.getUserId());
            stmt.setString(2, passenger.getPassportNumber());
            stmt.setString(3, passenger.getAddress());
            stmt.setString(4, passenger.getPreferredClass());
            stmt.executeUpdate();
        }

        logTransaction(conn, "SAVE_PASSENGER", passenger.getUserId(), "Passenger saved");
        return true;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


private void logTransaction(Connection conn, String type, String entityId, String description) throws Exception {
    String sql = "INSERT INTO transaction_logs (type, entity_id, description) VALUES (?, ?, ?)";

    try (var stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, type);
        stmt.setString(2, entityId);
        stmt.setString(3, description);
        stmt.executeUpdate();
    }
}


public Passenger getPassengerById(String passengerId) {
    String sql = """
        SELECT u.user_id, u.name, u.email, u.password_hash, u.phone_number,
               p.passport_number, p.address, p.preferred_class
        FROM users u
        JOIN passengers p ON u.user_id = p.user_id
        WHERE u.user_id = ?
    """;

    try (Connection conn = SystemDatabase.connect();
         var stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, passengerId);
        var rs = stmt.executeQuery();

        if (rs.next()) {
            return new Passenger(
                rs.getString("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("phone_number"),
                rs.getString("passport_number")
            );
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}

public Passenger getPassengerByEmail(String email) {
    String sql = """
        SELECT u.user_id, u.name, u.email, u.password_hash, u.phone_number,
               p.passport_number, p.address, p.preferred_class
        FROM users u
        JOIN passengers p ON u.user_id = p.user_id
        WHERE LOWER(u.email) = LOWER(?)
    """;

    try (Connection conn = SystemDatabase.connect();
         var stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, email);
        var rs = stmt.executeQuery();

        if (rs.next()) {
            return new Passenger(
                rs.getString("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("phone_number"),
                rs.getString("passport_number")
            );
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}

public boolean updatePassenger(Passenger passenger) {
    if (passenger == null || passenger.getUserId() == null) return false;

    String updateUserSql = """
        UPDATE users SET
            name = ?, email = ?, password_hash = ?, phone_number = ?
        WHERE user_id = ?
    """;

    String updatePassengerSql = """
        UPDATE passengers SET
            passport_number = ?, address = ?, preferred_class = ?
        WHERE user_id = ?
    """;

    try (Connection conn = SystemDatabase.connect()) {

        try (var stmt = conn.prepareStatement(updateUserSql)) {
            stmt.setString(1, passenger.getName());
            stmt.setString(2, passenger.getEmail());
            stmt.setString(3, passenger.getPassword());
            stmt.setString(4, passenger.getPhoneNumber());
            stmt.setString(5, passenger.getUserId());
            stmt.executeUpdate();
        }

        try (var stmt = conn.prepareStatement(updatePassengerSql)) {
            stmt.setString(1, passenger.getPassportNumber());
            stmt.setString(2, passenger.getAddress());
            stmt.setString(3, passenger.getPreferredClass());
            stmt.setString(4, passenger.getUserId());
            stmt.executeUpdate();
        }

        logTransaction(conn, "UPDATE_PASSENGER", passenger.getUserId(), "Passenger updated");
        return true;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


public boolean deletePassenger(String passengerId) {
    String sql = "DELETE FROM users WHERE user_id = ?";

    try (Connection conn = SystemDatabase.connect();
         var stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, passengerId);
        int deleted = stmt.executeUpdate();

        if (deleted > 0) {
            logTransaction(conn, "DELETE_PASSENGER", passengerId, "Passenger deleted");
            return true;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}

public List<Passenger> getAllPassengers() {
    List<Passenger> list = new ArrayList<>();

    String sql = """
        SELECT u.user_id, u.name, u.email, u.password_hash, u.phone_number,
               p.passport_number, p.address, p.preferred_class
        FROM users u
        JOIN passengers p ON u.user_id = p.user_id
    """;

    try (Connection conn = SystemDatabase.connect();
         var stmt = conn.prepareStatement(sql);
         var rs = stmt.executeQuery()) {

        while (rs.next()) {
            list.add(new Passenger(
                rs.getString("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("phone_number"),
                rs.getString("passport_number")
            ));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

// ADMIN

public boolean saveAdmin(Admin admin) {
    if (admin == null || admin.getUserId() == null) return false;

    String upsertUserSql = """
        INSERT INTO users (user_id, name, email, password_hash, phone_number)
        VALUES (?, ?, ?, ?, ?)
        ON CONFLICT (user_id) DO UPDATE SET
            name = EXCLUDED.name,
            email = EXCLUDED.email,
            password_hash = EXCLUDED.password_hash,
            phone_number = EXCLUDED.phone_number
    """;

    String upsertAdminSql = """
        INSERT INTO admins (user_id, role, department)
        VALUES (?, ?, ?)
        ON CONFLICT (user_id) DO UPDATE SET
            role = EXCLUDED.role,
            department = EXCLUDED.department
    """;

    try (Connection conn = SystemDatabase.connect()) {

        // Insert/update users row
        try (var stmt = conn.prepareStatement(upsertUserSql)) {
            stmt.setString(1, admin.getUserId());
            stmt.setString(2, admin.getName());
            stmt.setString(3, admin.getEmail());
            stmt.setString(4, admin.getPassword());
            stmt.setString(5, admin.getPhoneNumber());
            stmt.executeUpdate();
        }

        // Insert/update admins row
        try (var stmt = conn.prepareStatement(upsertAdminSql)) {
            stmt.setString(1, admin.getUserId());
            stmt.setString(2, admin.getAdminLevel());
            stmt.setString(3, admin.getDepartment());
            stmt.executeUpdate();
        }

        logTransaction(conn, "SAVE_ADMIN", admin.getUserId(), "Admin saved");
        return true;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public Admin getAdminById(String adminId) {
    String sql = """
        SELECT u.user_id, u.name, u.email, u.password_hash, u.phone_number,
               a.role, a.department
        FROM users u
        JOIN admins a ON u.user_id = a.user_id
        WHERE u.user_id = ?
    """;

    try (Connection conn = SystemDatabase.connect();
         var stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, adminId);
        var rs = stmt.executeQuery();

        if (rs.next()) {
            return new Admin(
                rs.getString("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("phone_number"),
                rs.getString("role")
            );
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}


public Admin getAdminByEmail(String email) {
    String sql = """
        SELECT u.user_id, u.name, u.email, u.password_hash, u.phone_number,
               a.role, a.department
        FROM users u
        JOIN admins a ON u.user_id = a.user_id
        WHERE LOWER(u.email) = LOWER(?)
    """;

    try (Connection conn = SystemDatabase.connect();
         var stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, email);
        var rs = stmt.executeQuery();

        if (rs.next()) {
            return new Admin(
                rs.getString("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("phone_number"),
                rs.getString("role")
            );
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}

// Staff
public boolean saveAirlineStaff(AirlineStaff staff) {
    if (staff == null || staff.getUserId() == null) return false;

    String upsertUserSql = """
        INSERT INTO users (user_id, name, email, password_hash, phone_number)
        VALUES (?, ?, ?, ?, ?)
        ON CONFLICT (user_id) DO UPDATE SET
            name = EXCLUDED.name,
            email = EXCLUDED.email,
            password_hash = EXCLUDED.password_hash,
            phone_number = EXCLUDED.phone_number
    """;

    String upsertStaffSql = """
        INSERT INTO airline_staff (user_id, position, assigned_gate)
        VALUES (?, ?, ?)
        ON CONFLICT (user_id) DO UPDATE SET
            position = EXCLUDED.position,
            assigned_gate = EXCLUDED.assigned_gate
    """;

    try (Connection conn = SystemDatabase.connect()) {

        // Insert/update users row
        try (var stmt = conn.prepareStatement(upsertUserSql)) {
            stmt.setString(1, staff.getUserId());
            stmt.setString(2, staff.getName());
            stmt.setString(3, staff.getEmail());
            stmt.setString(4, staff.getPassword());
            stmt.setString(5, staff.getPhoneNumber());
            stmt.executeUpdate();
        }

        // Insert/update airline_staff row
        try (var stmt = conn.prepareStatement(upsertStaffSql)) {
            stmt.setString(1, staff.getUserId());
            stmt.setString(2, staff.getRole());
            stmt.setString(3, staff.getAssignedGate());
            stmt.executeUpdate();
        }

        logTransaction(conn, "SAVE_STAFF", staff.getUserId(), "Airline staff saved");
        return true;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public AirlineStaff getAirlineStaffById(String staffId) {
    String sql = """
        SELECT u.user_id, u.name, u.email, u.password_hash, u.phone_number,
               s.position, s.assigned_gate
        FROM users u
        JOIN airline_staff s ON u.user_id = s.user_id
        WHERE u.user_id = ?
    """;

    try (Connection conn = SystemDatabase.connect();
         var stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, staffId);
        var rs = stmt.executeQuery();

        if (rs.next()) {
            return new AirlineStaff(
                rs.getString("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("phone_number"),
                rs.getString("position"),
                rs.getString("assigned_gate")
            );
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}

public AirlineStaff getAirlineStaffByEmail(String email) {
    String sql = """
        SELECT u.user_id, u.name, u.email, u.password_hash, u.phone_number,
               s.position, s.assigned_gate
        FROM users u
        JOIN airline_staff s ON u.user_id = s.user_id
        WHERE LOWER(u.email) = LOWER(?)
    """;

    try (Connection conn = SystemDatabase.connect();
         var stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, email);
        var rs = stmt.executeQuery();

        if (rs.next()) {
            return new AirlineStaff(
                rs.getString("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("phone_number"),
                rs.getString("position"),
                rs.getString("assigned_gate")
            );
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}

public boolean saveFlight(Flight flight) {
    if (flight == null || flight.getFlightId() == null) {
        return false;
    }

    String sql = """
        INSERT INTO flights (
            flight_id, flight_number, origin, destination,
            departure_time, arrival_time, aircraft_type,
            total_seats, available_seats, class_prices,
            status, gate
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, to_json(?::json), ?, ?)
    """;

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, flight.getFlightId());
        stmt.setString(2, flight.getFlightNumber());
        stmt.setString(3, flight.getOrigin());
        stmt.setString(4, flight.getDestination());
        stmt.setTimestamp(5, Timestamp.valueOf(flight.getDepartureTime()));
        stmt.setTimestamp(6, Timestamp.valueOf(flight.getArrivalTime()));
        stmt.setString(7, flight.getAircraftType());
        stmt.setInt(8, flight.getTotalSeats());
        stmt.setInt(9, flight.getAvailableSeats());
        stmt.setObject(10, flight.getClassPrices()); // must be JSON string like {"Economy":100}
        stmt.setString(11, flight.getStatus());
        stmt.setString(12, flight.getGate());

        stmt.executeUpdate();
        logTransaction(conn, "SAVE_FLIGHT", flight.getFlightId(), "Flight saved: " + flight.getFlightNumber());
        return true;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public Flight getFlightById(String flightId) {
    String sql = "SELECT * FROM flights WHERE flight_id = ?";

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, flightId);

        var rs = stmt.executeQuery();
        if (rs.next()) {
            return mapRowToFlight(rs);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}


public Flight getFlightByNumber(String flightNumber) {
    String sql = "SELECT * FROM flights WHERE LOWER(flight_number) = LOWER(?)";

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, flightNumber);

        var rs = stmt.executeQuery();
        if (rs.next()) {
            return mapRowToFlight(rs);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

public boolean updateFlight(Flight flight) {
    if (flight == null || flight.getFlightId() == null) {
        return false;
    }

    String sql = """
        UPDATE flights SET
            flight_number = ?, origin = ?, destination = ?,
            departure_time = ?, arrival_time = ?, aircraft_type = ?,
            total_seats = ?, available_seats = ?, class_prices = to_json(?::json),
            status = ?, gate = ?
        WHERE flight_id = ?
    """;

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, flight.getFlightNumber());
        stmt.setString(2, flight.getOrigin());
        stmt.setString(3, flight.getDestination());
        stmt.setTimestamp(4, Timestamp.valueOf(flight.getDepartureTime()));
        stmt.setTimestamp(5, Timestamp.valueOf(flight.getArrivalTime()));
        stmt.setString(6, flight.getAircraftType());
        stmt.setInt(7, flight.getTotalSeats());
        stmt.setInt(8, flight.getAvailableSeats());
        stmt.setObject(9, flight.getClassPrices());
        stmt.setString(10, flight.getStatus());
        stmt.setString(11, flight.getGate());
        stmt.setString(12, flight.getFlightId());

        int updated = stmt.executeUpdate();

        if (updated > 0) {
            logTransaction(conn, "UPDATE_FLIGHT", flight.getFlightId(), "Flight updated");
            return true;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}

public boolean deleteFlight(String flightId) {
    String sql = "DELETE FROM flights WHERE flight_id = ?";

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, flightId);

        int rows = stmt.executeUpdate();

        if (rows > 0) {
            logTransaction(conn, "DELETE_FLIGHT", flightId, "Flight deleted");
            return true;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}

public List<Flight> getAllFlights() {
    List<Flight> list = new ArrayList<>();

    String sql = "SELECT * FROM flights";

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql);
         var rs = stmt.executeQuery()) {

        while (rs.next()) {
            list.add(mapRowToFlight(rs));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

public List<Flight> searchFlights(String origin, String destination, LocalDateTime date) {
    List<Flight> result = new ArrayList<>();

    String sql = """
        SELECT * FROM flights
        WHERE LOWER(origin) = LOWER(?)
        AND LOWER(destination) = LOWER(?)
        AND DATE(departure_time) = DATE(?)
        AND status = 'SCHEDULED'
        AND available_seats > 0
    """;

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, origin);
        stmt.setString(2, destination);
        stmt.setTimestamp(3, Timestamp.valueOf(date));

        var rs = stmt.executeQuery();

        while (rs.next()) {
            result.add(mapRowToFlight(rs));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return result;
}


private Flight mapRowToFlight(ResultSet rs) throws Exception {
    Flight f = new Flight();

    f.setFlightId(rs.getString("flight_id"));
    f.setFlightNumber(rs.getString("flight_number"));
    f.setOrigin(rs.getString("origin"));
    f.setDestination(rs.getString("destination"));
    f.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
    f.setArrivalTime(rs.getTimestamp("arrival_time").toLocalDateTime());
    f.setAircraftType(rs.getString("aircraft_type"));
    f.setTotalSeats(rs.getInt("total_seats"));
    f.setAvailableSeats(rs.getInt("available_seats"));
    jsonToPriceMap(rs.getString("class_prices")); // JSONB comes as string
    f.setStatus(rs.getString("status"));
    f.setGate(rs.getString("gate"));

    return f;
}

public Map<String, Integer> jsonToPriceMap(String json) {
    Map<String, Integer> map = new HashMap<>();

    JSONObject obj = new JSONObject(json);

    for (String key : obj.keySet()) {
        double value = obj.getDouble(key);
        map.put(key, (int) value);  // convert 100.0 → 100
    }

    return map;
}

public boolean savePayment(Payment payment) {
    if (payment == null || payment.getPaymentId() == null) {
        return false;
    }

    String sql = """
        INSERT INTO payments (
            payment_id, booking_id, passenger_id, amount, payment_method,
            status, payment_date, transaction_reference, card_last_four_digits,
            fraud_detected, refund_reason, refund_date
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, payment.getPaymentId());
        stmt.setString(2, payment.getBookingId());
        stmt.setString(3, payment.getPassengerId());
        stmt.setDouble(4, payment.getAmount());
        stmt.setString(5, payment.getPaymentMethod());
        stmt.setString(6, payment.getStatus());

        stmt.setTimestamp(7, payment.getPaymentDate() != null
                ? Timestamp.valueOf(payment.getPaymentDate())
                : null);

        stmt.setString(8, payment.getTransactionReference());
        stmt.setString(9, payment.getCardLastFourDigits());
        stmt.setBoolean(10, payment.isFraudDetected());
        stmt.setString(11, payment.getRefundReason());

        stmt.setTimestamp(12, payment.getRefundDate() != null
                ? Timestamp.valueOf(payment.getRefundDate())
                : null);

        stmt.executeUpdate();

        logTransaction(conn, "SAVE_PAYMENT", payment.getPaymentId(),
                "Payment saved: " + payment.getStatus());

        return true;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public Payment getPaymentById(String paymentId) {
    String sql = "SELECT * FROM payments WHERE payment_id = ?";

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, paymentId);
        var rs = stmt.executeQuery();

        if (rs.next()) {
            return mapRowToPayment(rs);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}

public Payment getPaymentByBookingId(String bookingId) {
    String sql = "SELECT * FROM payments WHERE booking_id = ? LIMIT 1";

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, bookingId);

        var rs = stmt.executeQuery();
        if (rs.next()) {
            return mapRowToPayment(rs);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}

public boolean updatePayment(Payment payment) {
    if (payment == null || payment.getPaymentId() == null) {
        return false;
    }

    String sql = """
        UPDATE payments SET
            booking_id = ?, passenger_id = ?, amount = ?, payment_method = ?,
            status = ?, payment_date = ?, transaction_reference = ?,
            card_last_four_digits = ?, fraud_detected = ?, refund_reason = ?,
            refund_date = ?
        WHERE payment_id = ?
    """;

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, payment.getBookingId());
        stmt.setString(2, payment.getPassengerId());
        stmt.setDouble(3, payment.getAmount());
        stmt.setString(4, payment.getPaymentMethod());
        stmt.setString(5, payment.getStatus());

        stmt.setTimestamp(6, payment.getPaymentDate() != null
                ? Timestamp.valueOf(payment.getPaymentDate())
                : null);

        stmt.setString(7, payment.getTransactionReference());
        stmt.setString(8, payment.getCardLastFourDigits());
        stmt.setBoolean(9, payment.isFraudDetected());
        stmt.setString(10, payment.getRefundReason());

        stmt.setTimestamp(11, payment.getRefundDate() != null
                ? Timestamp.valueOf(payment.getRefundDate())
                : null);

        stmt.setString(12, payment.getPaymentId());

        int updated = stmt.executeUpdate();
        if (updated > 0) {
            logTransaction(conn, "UPDATE_PAYMENT", payment.getPaymentId(),
                    "Payment updated: " + payment.getStatus());
            return true;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}

public List<Payment> getAllPayments() {
    List<Payment> list = new ArrayList<>();

    String sql = "SELECT * FROM payments";

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql);
         var rs = stmt.executeQuery()) {

        while (rs.next()) {
            list.add(mapRowToPayment(rs));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

public Payment mapRowToPayment(ResultSet rs) throws Exception {
    Payment p = new Payment();

    p.setPaymentId(rs.getString("payment_id"));
    p.setBookingId(rs.getString("booking_id"));
    p.setPassengerId(rs.getString("passenger_id"));
    p.setAmount(rs.getDouble("amount"));
    p.setPaymentMethod(rs.getString("payment_method"));
    p.setStatus(rs.getString("status"));

    Timestamp payTs = rs.getTimestamp("payment_date");
    p.setPaymentDate(payTs != null ? payTs.toLocalDateTime() : null);

    p.setTransactionReference(rs.getString("transaction_reference"));
    p.setCardLastFourDigits(rs.getString("card_last_four_digits"));
    p.setFraudDetected(rs.getBoolean("fraud_detected"));
    p.setRefundReason(rs.getString("refund_reason"));

    Timestamp refundTs = rs.getTimestamp("refund_date");
    p.setRefundDate(refundTs != null ? refundTs.toLocalDateTime() : null);

    return p;
}

private void logTransaction(String type, String entityId, String description) throws Exception{
    String sql = """
        INSERT INTO transaction_logs (type, entity_id, description, created_at)
        VALUES (?, ?, ?, NOW())
    """;

    try (PreparedStatement stmt = SystemDatabase.connect().prepareStatement(sql)) {
        stmt.setString(1, type);
        stmt.setString(2, entityId);
        stmt.setString(3, description);
        stmt.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException("Error saving transaction log", e);
    }
}

public List<TransactionLog> getTransactionLogs() throws Exception{
    List<TransactionLog> logs = new ArrayList<>();

    String sql = "SELECT * FROM transaction_logs ORDER BY created_at DESC";

    try (PreparedStatement stmt = SystemDatabase.connect().prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            logs.add(mapRowToTransactionLog(rs));
        }

    } catch (SQLException e) {
        throw new RuntimeException("Error retrieving logs", e);
    }

    return logs;
}

public List<TransactionLog> getTransactionLogsByType(String type) throws Exception{
    List<TransactionLog> logs = new ArrayList<>();

    String sql = "SELECT * FROM transaction_logs WHERE type = ? ORDER BY created_at DESC";

    try (PreparedStatement stmt = SystemDatabase.connect().prepareStatement(sql)) {
        stmt.setString(1, type);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                logs.add(mapRowToTransactionLog(rs));
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException("Error retrieving logs by type", e);
    }

    return logs;
}

public class TransactionLog {
    private String logId;
    private String type;
    private String entityId;
    private String description;
    private LocalDateTime timestamp;

    // Constructor used when creating a new log
    public TransactionLog(String type, String entityId, String description) {
        this.logId = UUID.randomUUID().toString();
        this.type = type;
        this.entityId = entityId;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    // Default constructor (needed for mapping from ResultSet)
    public TransactionLog() {
    }

    // Getters
    public String getLogId() {
        return logId;
    }

    public String getType() {
        return type;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Setters (needed for mapping from database)
    public void setLogId(String logId) {
        this.logId = logId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s: %s", timestamp, type, entityId, description);
    }
}

private TransactionLog mapRowToTransactionLog(ResultSet rs) throws SQLException {
    TransactionLog log = new TransactionLog("", "", "");

    log.setLogId(rs.getString("log_id")); // UUID
    log.setType(rs.getString("type"));
    log.setEntityId(rs.getString("entity_id"));
    log.setDescription(rs.getString("description"));

    return log;
}

public Map<String, Object> getSystemStatistics() {
    Map<String, Object> stats = new HashMap<>();

    String sql = """
        SELECT
            (SELECT COUNT(*) FROM passengers) AS total_passengers,
            (SELECT COUNT(*) FROM flights) AS total_flights,
            (SELECT COUNT(*) FROM bookings) AS total_bookings,
            (SELECT COUNT(*) FROM bookings WHERE status = 'CONFIRMED') AS confirmed_bookings,
            (SELECT COUNT(*) FROM bookings WHERE status = 'CANCELLED') AS cancelled_bookings,
            (SELECT COALESCE(SUM(amount),0) FROM payments WHERE status = 'SUCCESS') AS total_revenue
    """;

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        if (rs.next()) {
            stats.put("totalPassengers", rs.getInt("total_passengers"));
            stats.put("totalFlights", rs.getInt("total_flights"));
            stats.put("totalBookings", rs.getInt("total_bookings"));
            stats.put("confirmedBookings", rs.getInt("confirmed_bookings"));
            stats.put("cancelledBookings", rs.getInt("cancelled_bookings"));
            stats.put("totalRevenue", rs.getDouble("total_revenue"));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return stats;
}
public boolean saveBooking(Booking booking) {
    if (booking == null || booking.getBookingId() == null) {
        return false;
    }

    String sql = """
        INSERT INTO bookings (
            booking_id, passenger_id, flight_id,
            seat_number, status, booking_date
        ) VALUES (?, ?, ?, ?, ?, ?)
    """;

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, booking.getBookingId());
        stmt.setString(2, booking.getPassengerId());
        stmt.setString(3, booking.getFlightId());
        stmt.setString(4, booking.getSeatNumber());
        stmt.setString(5, booking.getStatus());
        stmt.setTimestamp(6, Timestamp.valueOf(booking.getBookingDate()));

        stmt.executeUpdate();

        logTransaction("SAVE_BOOKING", booking.getBookingId(),
                "Booking saved for passenger: " + booking.getPassengerId());

        return true;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public Booking getBookingById(String bookingId) {
    String sql = "SELECT * FROM bookings WHERE booking_id = ?";

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, bookingId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return mapRowToBooking(rs);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}

public boolean updateBooking(Booking booking) {
    if (booking == null || booking.getBookingId() == null) {
        return false;
    }

    String sql = """
        UPDATE bookings
        SET passenger_id = ?, flight_id = ?, seat_number = ?,
            status = ?, booking_date = ?
        WHERE booking_id = ?
    """;

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, booking.getPassengerId());
        stmt.setString(2, booking.getFlightId());
        stmt.setString(3, booking.getSeatNumber());
        stmt.setString(4, booking.getStatus());
        stmt.setTimestamp(5, Timestamp.valueOf(booking.getBookingDate()));
        stmt.setString(6, booking.getBookingId());

        int updated = stmt.executeUpdate();

        if (updated > 0) {
            logTransaction("UPDATE_BOOKING", booking.getBookingId(),
                    "Booking updated: " + booking.getStatus());
            return true;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}

public List<Booking> getBookingsByPassengerId(String passengerId) {
    List<Booking> list = new ArrayList<>();

    String sql = "SELECT * FROM bookings WHERE passenger_id = ?";

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, passengerId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            list.add(mapRowToBooking(rs));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}


public List<Booking> getBookingsByFlightId(String flightId) {
    List<Booking> list = new ArrayList<>();

    String sql = """
        SELECT * FROM bookings
        WHERE flight_id = ? AND status = 'CONFIRMED'
    """;

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, flightId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            list.add(mapRowToBooking(rs));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

public List<Booking> getAllBookings() {
    List<Booking> list = new ArrayList<>();

    String sql = "SELECT * FROM bookings";

    try (Connection conn = SystemDatabase.connect();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            list.add(mapRowToBooking(rs));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

private Booking mapRowToBooking(ResultSet rs) throws Exception {
    Booking booking = new Booking();

    booking.setBookingId(rs.getString("booking_id"));
    booking.setPassengerId(rs.getString("passenger_id"));
    booking.setFlightId(rs.getString("flight_id"));
    booking.setSeatNumber(rs.getString("seat_number"));
    booking.setStatus(rs.getString("status"));

    Timestamp ts = rs.getTimestamp("booking_date");
    if (ts != null) {
        booking.setBookingDate(ts.toLocalDateTime());
    }

    return booking;
}

public void performBackup(){
    System.out.println("Handled By Supabase");
}

public boolean restoreFromBackup() {
    //handled by supabase    
    return true;
}
//     public static void main(String[] args) throws Exception {
//         Connection conn = Supabase.connect();
//         System.out.println("Connected to Supabase PostgreSQL!");
// }

    private static String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

private static void initializeDefaultData() {
    String defaultAdminId = "ADMIN001";
    String email = "admin@nocrashairlines.com";

    try (Connection conn = SystemDatabase.connect()) {

        // 1. Check if admin already exists
        String checkSql = "SELECT user_id FROM users WHERE user_id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, defaultAdminId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("Default admin already exists. Skipping initialization.");
                return;
            }
        }

        // 2. Hash password using your Java method
        String hashedPassword = hashPassword("Admin@123");

        // 3. Insert into users table
        String insertUserSql = """
            INSERT INTO users (
                user_id, name, email, password_hash,
                phone_number, created_at, last_login,
                failed_login_attempts, account_locked
            ) VALUES (?, ?, ?, ?, ?, NOW(), NULL, 0, FALSE)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(insertUserSql)) {
            stmt.setString(1, defaultAdminId);
            stmt.setString(2, "System Admin");
            stmt.setString(3, email);
            stmt.setString(4, hashedPassword);
            stmt.setString(5, "+1234567890");
            stmt.executeUpdate();
        }

        // 4. Insert into admins table
        String insertAdminSql = """
            INSERT INTO admins (
                user_id, role, department
            ) VALUES (?, ?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(insertAdminSql)) {
            stmt.setString(1, defaultAdminId);
            stmt.setString(2, "SUPER_ADMIN");
            stmt.setString(3, "Administration");
            stmt.executeUpdate();
        }

        System.out.println("Default admin successfully created.");

    } catch (Exception e) {
        e.printStackTrace();
    }
}

public static void main(String[] args) {
    SystemDatabase.initializeDefaultData();
}

}