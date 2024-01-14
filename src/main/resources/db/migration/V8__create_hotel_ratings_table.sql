CREATE TABLE IF NOT EXISTS hotel_ratings (
    id serial PRIMARY KEY,
    rate integer NOT NULL,
    user_id integer NOT NULL,
    hotel_id integer NOT NULL,

    CONSTRAINT user_id FOREIGN KEY (user_id)
    REFERENCES users (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
    CONSTRAINT hotel_id FOREIGN KEY (hotel_id)
    REFERENCES hotels (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
    CONSTRAINT UQ_user_id_hotel_id UNIQUE(user_id, hotel_id)
);
