CREATE TABLE checkins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_id BIGINT NOT NULL,
    checkin_time DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL
);