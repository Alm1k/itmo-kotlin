CREATE TABLE IF NOT EXISTS request_statuses (
    id serial PRIMARY KEY,
    status character varying NOT NULL
);

INSERT INTO request_statuses
    (id, status)
SELECT 1, 'PENDING'
WHERE
    NOT EXISTS (
        SELECT id FROM request_statuses WHERE id = 1
    );

INSERT INTO request_statuses
    (id, status)
SELECT 2, 'IN_PROGRESS'
WHERE
    NOT EXISTS (
        SELECT id FROM request_statuses WHERE id = 2
    );

INSERT INTO request_statuses
    (id, status)
SELECT 3, 'DONE'
WHERE
    NOT EXISTS (
        SELECT id FROM request_statuses WHERE id = 3
    );