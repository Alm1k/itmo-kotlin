CREATE SEQUENCE IF NOT EXISTS room_booking_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 10000
    CACHE 1;

CREATE TABLE IF NOT EXISTS room_bookings (
    id integer NOT NULL DEFAULT nextval('room_booking_id_seq'::regclass),
    from_date character varying(50) NOT NULL,
    to_date character varying(50) NOT NULL,
    room_id integer NOT NULL,
    user_id integer NOT NULL,

    CONSTRAINT room_bookings_pkey PRIMARY KEY (id),
    CONSTRAINT room_id FOREIGN KEY (room_id)
    REFERENCES rooms (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
    CONSTRAINT user_id FOREIGN KEY (user_id)
    REFERENCES users (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

ALTER SEQUENCE room_booking_id_seq OWNED BY room_bookings.id;
