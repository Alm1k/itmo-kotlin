CREATE TABLE IF NOT EXISTS cleanings (
    id serial PRIMARY KEY,
    creation_date date NOT NULL,
    cleaner_id integer NOT NULL,
    room_id integer NOT NULL,
    hotel_id integer NOT NULL,
    is_done boolean NOT NULL DEFAULT (false),

    CONSTRAINT cleaner_id FOREIGN KEY (cleaner_id)
    REFERENCES cleaner_infos (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
    CONSTRAINT hotel_id FOREIGN KEY (hotel_id)
    REFERENCES hotels (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
    CONSTRAINT room_id FOREIGN KEY (room_id)
    REFERENCES rooms (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);
