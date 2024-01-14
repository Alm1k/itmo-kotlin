
CREATE TABLE IF NOT EXISTS users (
    id serial PRIMARY KEY,
    name character varying(50) NOT NULL,
    surname character varying(50) NOT NULL,
    bday character varying(50),
    login character varying(50) NOT NULL UNIQUE,
    password character varying(200) NOT NULL,
    email character varying(50) UNIQUE,
    role_id integer NOT NULL,

    CONSTRAINT role_id FOREIGN KEY (role_id)
    REFERENCES roles (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

