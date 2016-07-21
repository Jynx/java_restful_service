Steven Volocyk Restful Service
================

Contents
--------
This example is a basic Restful web service built using Java, Maven, Glassfish, PostgreSQL, Hibernate and Jersey.

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



=========================================================


