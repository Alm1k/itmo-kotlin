CREATE SEQUENCE IF NOT EXISTS user_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 10000
    CACHE 1;

CREATE TABLE IF NOT EXISTS users (
    name character varying(50) NOT NULL,
    surname character varying(50) NOT NULL,
    bday character varying(50),
    login character varying(50) NOT NULL,
    password character varying(75) NOT NULL,
    email character varying(75),
    id integer NOT NULL DEFAULT nextval('user_id_seq'::regclass),
    role_id integer,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT email UNIQUE (email),
    CONSTRAINT login UNIQUE (login)
--     CONSTRAINT role_id FOREIGN KEY (role_id)
--     REFERENCES roles (id) MATCH FULL
--     ON UPDATE CASCADE
--     ON DELETE RESTRICT
);

ALTER SEQUENCE user_id_seq OWNED BY users.id;
