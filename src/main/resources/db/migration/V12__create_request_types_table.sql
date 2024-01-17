CREATE TABLE IF NOT EXISTS request_types (
    id serial PRIMARY KEY,
    type character varying NOT NULL
);

INSERT INTO request_types
    (id, type)
SELECT 1, 'REFILL'
WHERE
    NOT EXISTS (
        SELECT id FROM request_types WHERE id = 1
    );

INSERT INTO request_types
    (id, type)
SELECT 2, 'REPAIR'
WHERE
    NOT EXISTS (
        SELECT id FROM request_types WHERE id = 2
    );

INSERT INTO request_types
    (id, type)
SELECT 3, 'REPLACEMENT'
WHERE
    NOT EXISTS (
        SELECT id FROM request_types WHERE id = 3
    );