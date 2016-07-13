chef-starter-project
=================

Chef Starter project

Description
------------
Implement a REST service to store, fetch, and update user records. A user record is a JSON hash like so:

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

Implementation Notes
--------------------
Acceptable implementation languages are Java, Ruby, Erlang, JavaScript, and Intercal.

Any design decisions not specified herein are fair game. Completed projects will be evaluated on how closely they follow the spec, their design, and cleanliness of implementation.

Completed projects must include a README with enough instructions for evaluators to build and run the code. Bonus points for builds which require minimal manual steps.

Remember this project should take a maximum of 8 hours to complete. Do not get hung up on scaling or persistence issues. This is a project used to evaluate your design and coding skills only.
