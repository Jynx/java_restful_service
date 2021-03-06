Steven Volocyk Restful Service -- Used as exercise to build some basic Java/API concepts at the time.
================
Contents
--------
This example is a basic Restful web service built using Java, Maven, Glassfish, Jetty, PostgreSQL, Hibernate, JUnit,
Jackson, Gson and Jersey/JAX-RS. 


TODO: Add input validation

The mapping of the URI path space is presented in the following table:

URI path                                | Resource class      | HTTP methods                                          | Notes
--------------------------------------- | ------------------- | ----------------------------------------------------- | --------------------------------------------------------
**_/users/{userid}_**                   |  UserResource       |  GET, POST, PUT, DELETE                               |  Returns, Creates, Deletes, or Updates a User Record.
**_/groups/{group name}_**              |  GroupResource      |  GET, POST, PUT, DELETE                               |  Returns a JSON list of Users, Creates, Updates, or Deletes Group records.

Description
------------

```
{"first_name": "Joe",
 "last_name": "Smith",
 "userid": "jsmith",
 "groups": ["admins", "users"]}
```

```GET /users/<userid>```
Returns the matching user record or 404 if none exist.

```POST /users/<userid>```
Creates a new user record. The body of the request should be a valid user record. POSTs to an existing user should be treated as errors and flagged with the appropriate HTTP status code.

```DELETE /users/<userid>```
Deletes a user record. Returns 404 if the user doesn't exist.

```PUT /users/<userid>```
Updates an existing user record. The body of the request should be a valid user record. PUTs to a non-existant user should return a 404.

```GET /groups/<group name>```
Returns a JSON list of userids containing the members of that group. Should return a 404 if the group doesn't exist or has no members.

```POST /groups/<group name>```
Creates a empty group. POSTs to an existing group should be treated as errors and flagged with the appropriate HTTP status code.

```PUT /groups/<group name>```
Updates the membership list for the group. The body of the request should be a JSON list describing the group's members.

```DELETE /groups/<group name>```
Removes all members from the named group. Should return a 404 for unknown groups.

SETUP
-------------------

OSX SETUP
------------
Install postgresql
```
brew install postgresql
```

If you don't have Java installed >= jdk 1.8 do the following to install jdk 1.8
```
brew install brew-cask
brew cask install java
```

Download the project zip. Unzip to any location.

SETUP POSTGRESQL
------------
IMPORTANT: You will see Unit Tests failing if a connection cannot be established to postgres, or JDK is not 1.8 or higher.

Start PSQL if not already started:
```
Start psql : pg_ctl -D /usr/local/var/postgres -l /usr/local/var/postgres/server.log start
```

- Configure your default database (which will likely be your admin login as owner name)
- Create a database named 'restdb' (this is the default db in the Hibernate configuration file) or name it anything you'd like.
  Just don't forget to update the db in the configuration file later.

- Create 3 tables either by using the below queries, by executing the sql file, "table_create.sql" included in the project, OR using liquibase.

LIQUIBASE
------------
- Configure the liquibase.properties file to match your DB credentials. This includes username, password, and URL.
- Run the following command and confirm that the tables were created correctly:

```
mvn liquibase:update
```

CREATING MANUALLY
------------

 ```
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

 ```

 Open hibernate.cfg.xml (src/main/resources) from root starter-jynx directory, and cofigure the DB connection. On OSX, username is
 the default username you set up the postgres owner to be, and password is empty. Make sure to change the db from restdb if you decided
 to create one using a different name.

BUILD AND START
------------

- Install all dependencies
```
mvn package
```

At this point you should see 18 passing tests, and some null pointers from a couple of Jars that will not impact the application running.

- Start server
```
mvn jetty:run
```
Feel free to test either using curl, or postman.

=========================================================


