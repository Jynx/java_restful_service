--liquibase formatted sql

  CREATE TABLE rest_user
  (
     id integer NOT NULL,
     user_id varchar(50) NOT NULL,
     first_name varchar(50) NOT NULL,
     last_name varchar(50) NOT NULL,
     CONSTRAINT rest_user1_pkey PRIMARY KEY (id)
  );

  CREATE TABLE rest_group
  (
     id integer NOT NULL,
     group_name varchar(50) NOT NULL,
     CONSTRAINT rest_group1_pkey PRIMARY KEY (id)
  );

  CREATE TABLE rest_user_group_mapping
  (
     id integer NOT NULL,
     group_name varchar(50) NOT NULL,
     user_id varchar(50) NOT NULL,
     user_unique_id integer NOT NULL,
     CONSTRAINT rest_user_group_mapping1_pkey PRIMARY KEY (id)
  );

--rollback DROP TABLE SampleTable;