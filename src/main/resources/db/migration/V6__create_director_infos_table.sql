CREATE TABLE IF NOT EXISTS director_infos (
    id serial PRIMARY KEY,
    director_id integer NOT NULL,

    CONSTRAINT director_id UNIQUE (director_id),
    CONSTRAINT director_id FOREIGN KEY (director_id)
    REFERENCES users (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);
