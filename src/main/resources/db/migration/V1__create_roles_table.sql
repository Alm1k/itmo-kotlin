CREATE SEQUENCE IF NOT EXISTS role_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 10000
    CACHE 1;

CREATE TABLE IF NOT EXISTS roles (
    id integer NOT NULL DEFAULT nextval('role_id_seq'::regclass),
    role character varying NOT NULL,
    CONSTRAINT roles_pkey PRIMARY KEY (id)
);

ALTER SEQUENCE role_id_seq OWNED BY roles.id;
