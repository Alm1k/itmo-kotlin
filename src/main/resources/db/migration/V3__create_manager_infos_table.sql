CREATE SEQUENCE IF NOT EXISTS manager_info_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 10000
    CACHE 1;

CREATE TABLE IF NOT EXISTS manager_infos (
    id integer NOT NULL DEFAULT nextval('manager_info_id_seq'::regclass),
    manager_id integer NOT NULL,

    CONSTRAINT manager_infos_pkey PRIMARY KEY (id),
    CONSTRAINT manager_id FOREIGN KEY (manager_id)
    REFERENCES users (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

ALTER SEQUENCE manager_info_id_seq OWNED BY manager_infos.id;
