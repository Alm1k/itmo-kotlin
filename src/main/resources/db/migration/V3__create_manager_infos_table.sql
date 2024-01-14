CREATE TABLE IF NOT EXISTS manager_infos (
    id serial PRIMARY KEY,
    manager_id integer NOT NULL,

    CONSTRAINT manager_id FOREIGN KEY (manager_id)
    REFERENCES users (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);
