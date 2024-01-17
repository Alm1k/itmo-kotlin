CREATE TABLE IF NOT EXISTS cleaner_infos (
    id serial PRIMARY KEY,
    cleaner_id integer NOT NULL,
    hotel_id integer NOT NULL,

    CONSTRAINT cleaner_id FOREIGN KEY (cleaner_id)
    REFERENCES users (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
    CONSTRAINT hotel_id FOREIGN KEY (hotel_id)
    REFERENCES hotels (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
    CONSTRAINT UQ_cleaner_id_hotel_id UNIQUE(cleaner_id, hotel_id)
);
