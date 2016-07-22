CREATE TABLE rest_user
(
   id integer PRIMARY KEY NOT NULL,
   user_id varchar(50) NOT NULL,
   first_name varchar(50) NOT NULL,
   last_name varchar(50) NOT NULL
);

CREATE TABLE rest_group
(
   id integer PRIMARY KEY NOT NULL,
   group_name varchar(50) NOT NULL
);

CREATE TABLE rest_user_group_mapping
(
   id integer PRIMARY KEY NOT NULL,
   group_name varchar(50) NOT NULL,
   user_id varchar(50) NOT NULL,
   user_unique_id integer NOT NULL
);