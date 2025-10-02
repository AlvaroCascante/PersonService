-- Migration for creating the person table
CREATE TABLE person (
    id UUID PRIMARY KEY,
    id_number VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL,
    gender VARCHAR(50) NOT NULL
);