CREATE SEQUENCE IF NOT EXISTS room_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 10000
    CACHE 1;

CREATE TABLE IF NOT EXISTS rooms (
    id integer NOT NULL DEFAULT nextval('room_id_seq'::regclass),
    number integer NOT NULL,
    capacity integer NOT NULL,
    floor integer NOT NULL,
    price double precision NOT NULL,
    is_vip boolean NOT NULL DEFAULT false,
    manager_info_id integer,

    CONSTRAINT rooms_pkey PRIMARY KEY (id),
    FOREIGN KEY (manager_info_id) REFERENCES manager_infos(id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

ALTER SEQUENCE room_id_seq OWNED BY rooms.id;
