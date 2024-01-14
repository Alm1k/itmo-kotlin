CREATE TABLE IF NOT EXISTS rooms (
    id serial PRIMARY KEY,
    number integer NOT NULL,
    capacity integer NOT NULL,
    floor integer NOT NULL,
    price double precision NOT NULL,
    is_vip boolean NOT NULL DEFAULT false,
    manager_info_id integer,

    FOREIGN KEY (manager_info_id) REFERENCES manager_infos(id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE CASCADE
);
