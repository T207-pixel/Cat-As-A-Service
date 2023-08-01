CREATE DATABASE cat_info;

create table cats_menu(
    id SERIAL PRIMARY KEY,
    dishName VARCHAR(255) UNIQUE NOT NULL
)

create table clients_fed(
    id SERIAL PRIMARY KEY,
    userName VARCHAR(255) NOT NULL,
    dishName VARCHAR(255) NOT NULL
)

create table feeders_strokers(
    id SERIAL PRIMARY KEY,
    userName VARCHAR(255) NOT NULL
)

SELECT * FROM cats_menu
SELECT * FROM clients_fed
SELECT * FROM feeders_strokers

DELETE FROM cats_menu
DELETE FROM clients_fed

