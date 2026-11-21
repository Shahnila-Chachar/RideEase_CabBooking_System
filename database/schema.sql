CREATE DATABASE IF NOT EXISTS rideease_db;
USE rideease_db;

-- 1. USERS
CREATE TABLE USERS (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('PASSENGER', 'DRIVER', 'ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. PASSENGERS
CREATE TABLE PASSENGERS (
    passenger_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_passenger_user
        FOREIGN KEY (user_id) REFERENCES USERS(user_id)
) ENGINE=InnoDB;

-- 3. DRIVERS
CREATE TABLE DRIVERS (
    driver_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    license_number VARCHAR(50) NOT NULL UNIQUE,
    city VARCHAR(100) NOT NULL,
    service_area VARCHAR(150) NOT NULL,
    is_online BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_driver_user
        FOREIGN KEY (user_id) REFERENCES USERS(user_id)
) ENGINE=InnoDB;

-- 4. VEHICLES
CREATE TABLE VEHICLES (
    vehicle_id INT AUTO_INCREMENT PRIMARY KEY,
    driver_id INT NOT NULL UNIQUE,
    vehicle_type ENUM('RICKSHAW', 'CAB') NOT NULL,
    vehicle_number VARCHAR(20) NOT NULL UNIQUE,
    model VARCHAR(100) NOT NULL,
    color VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicle_driver
        FOREIGN KEY (driver_id) REFERENCES DRIVERS(driver_id)
) ENGINE=InnoDB;

-- 5. BOOKINGS
CREATE TABLE BOOKINGS (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    passenger_id INT NOT NULL,
    driver_id INT NULL,
    vehicle_id INT NULL,
    pickup_location VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    vehicle_type ENUM('RICKSHAW', 'CAB') NOT NULL,
    estimated_fare DECIMAL(10,2) NOT NULL,
    final_fare DECIMAL(10,2) NULL,
    status ENUM(
        'PENDING',
        'ACCEPTED',
        'IN_PROGRESS',
        'COMPLETED',
        'CANCELLED',
        'NO_DRIVER_AVAILABLE'
    ) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_booking_passenger
        FOREIGN KEY (passenger_id) REFERENCES PASSENGERS(passenger_id),

    CONSTRAINT fk_booking_driver
        FOREIGN KEY (driver_id) REFERENCES DRIVERS(driver_id),

    CONSTRAINT fk_booking_vehicle
        FOREIGN KEY (vehicle_id) REFERENCES VEHICLES(vehicle_id),

    CONSTRAINT chk_estimated_fare
        CHECK (estimated_fare >= 0),

    CONSTRAINT chk_final_fare
        CHECK (final_fare IS NULL OR final_fare >= 0)
) ENGINE=InnoDB;

-- 6. PAYMENTS
CREATE TABLE PAYMENTS (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('CASH', 'ONLINE') NOT NULL,
    status ENUM('PENDING', 'SUCCESSFUL', 'FAILED')
        NOT NULL DEFAULT 'PENDING',
    paid_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payment_booking
        FOREIGN KEY (booking_id) REFERENCES BOOKINGS(booking_id),

    CONSTRAINT chk_payment_amount
        CHECK (amount >= 0)
) ENGINE=InnoDB;

-- 7. NOTIFICATIONS
CREATE TABLE NOTIFICATIONS (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    booking_id INT NULL,
    message VARCHAR(255) NOT NULL,
    notification_type ENUM('RIDE', 'PAYMENT', 'SYSTEM') NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id) REFERENCES USERS(user_id),

    CONSTRAINT fk_notification_booking
        FOREIGN KEY (booking_id) REFERENCES BOOKINGS(booking_id)
) ENGINE=InnoDB;

-- 8. RIDE_ASSIGNMENT_ATTEMPTS
CREATE TABLE RIDE_ASSIGNMENT_ATTEMPTS (
    attempt_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    driver_id INT NOT NULL,
    attempt_status ENUM(
        'PENDING',
        'ACCEPTED',
        'REJECTED',
        'EXPIRED'
    ) NOT NULL DEFAULT 'PENDING',
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP NULL,

    CONSTRAINT fk_attempt_booking
        FOREIGN KEY (booking_id) REFERENCES BOOKINGS(booking_id),

    CONSTRAINT fk_attempt_driver
        FOREIGN KEY (driver_id) REFERENCES DRIVERS(driver_id),

    CONSTRAINT uq_booking_driver
        UNIQUE (booking_id, driver_id)
) ENGINE=InnoDB;
