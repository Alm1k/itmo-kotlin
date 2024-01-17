CREATE TABLE IF NOT EXISTS roles (
    id serial PRIMARY KEY,
    role character varying NOT NULL
);

INSERT INTO roles
    (id, role)
SELECT 1, 'DIRECTOR'
WHERE
    NOT EXISTS (
        SELECT id FROM roles WHERE id = 1
    );

INSERT INTO roles
    (id, role)
SELECT 2, 'MANAGER'
WHERE
    NOT EXISTS (
        SELECT id FROM roles WHERE id = 2
    );

INSERT INTO roles
    (id, role)
SELECT 3, 'USER'
WHERE
    NOT EXISTS (
        SELECT id FROM roles WHERE id = 3
    );

INSERT INTO roles
    (id, role)
SELECT 4, 'CLEANER'
WHERE
    NOT EXISTS (
        SELECT id FROM roles WHERE id = 4
    );