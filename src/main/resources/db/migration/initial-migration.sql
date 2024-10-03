CREATE TABLE IF NOT EXISTS branches (
                                        id SERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    workplace_count INTEGER NOT NULL,
    type VARCHAR(20) NOT NULL,
    parent_branch_id INTEGER REFERENCES branches(id)
    );

INSERT INTO branches (name, address, workplace_count, type)
VALUES ('Головний офіс', 'вул. Центральна, 1', 10, 'MAIN_OFFICE');