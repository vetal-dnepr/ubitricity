DROP TABLE IF EXISTS carparks;
CREATE TABLE carparks(id serial PRIMARY KEY, name VARCHAR(255), address VARCHAR(255), charging_points jsonb, version int);