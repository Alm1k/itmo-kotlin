CREATE TABLE IF NOT EXISTS room_bookings (
    id serial PRIMARY KEY,
    from_date character varying(50) NOT NULL,
    to_date character varying(50) NOT NULL,
    room_id integer NOT NULL,
    user_id integer NOT NULL,

    CONSTRAINT room_id FOREIGN KEY (room_id)
    REFERENCES rooms (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
    CONSTRAINT user_id FOREIGN KEY (user_id)
    REFERENCES users (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE CASCADE
);
