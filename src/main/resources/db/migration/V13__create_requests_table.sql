CREATE TABLE IF NOT EXISTS requests (
    id serial PRIMARY KEY,
    from_client_id integer NOT NULL,
    hotel_id integer NOT NULL,
    room_id integer NOT NULL,
    request_type_id integer NOT NULL,
    request_status_id integer NOT NULL,
    additional_info character varying(100) NOT NULL,

    CONSTRAINT from_client_id FOREIGN KEY (from_client_id)
    REFERENCES users (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
    CONSTRAINT request_type_id FOREIGN KEY (request_type_id)
    REFERENCES request_types (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
    CONSTRAINT request_status_id FOREIGN KEY (request_status_id)
    REFERENCES request_statuses (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
    CONSTRAINT room_id FOREIGN KEY (room_id)
    REFERENCES rooms (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);
