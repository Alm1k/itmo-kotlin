CREATE TABLE IF NOT EXISTS hotels (
    id serial PRIMARY KEY,
    name character varying(50) NOT NULL,
    stage_count integer NOT NULL,
	director_info_id integer NOT NULL,

    CONSTRAINT director_info_id FOREIGN KEY (director_info_id)
    REFERENCES director_infos (id) MATCH FULL
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);
