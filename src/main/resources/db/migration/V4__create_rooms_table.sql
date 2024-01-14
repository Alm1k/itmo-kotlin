CREATE TABLE IF NOT EXISTS rooms (
    id serial PRIMARY KEY,
    number integer NOT NULL,
    capacity integer NOT NULL,
    floor integer NOT NULL,
    price double precision NOT NULL,
    is_vip boolean NOT NULL DEFAULT false,
    manager_info_id integer,
    hotel_id integer NOT NULL,

    FOREIGN KEY (manager_info_id) REFERENCES manager_infos(id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE CASCADE,
    CONSTRAINT hotel_id FOREIGN KEY (hotel_id)
    REFERENCES hotels (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
    CONSTRAINT UQ_number_hotel_id UNIQUE(number, hotel_id)
);
