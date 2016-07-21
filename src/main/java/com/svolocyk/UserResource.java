package com.svolocyk;

import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Steven Volocyk on 7/17/2016.
 */
@Path("users")
public class UserResource {

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("id") String userId) {
        Session session = HibernateUtility.getSessionFactory().openSession();
        if (!HibernateUtility.userExists(userId, session)) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found for UserId: " + userId).build();
        }
        List<User> list = HibernateUtility.getUserListForId(session, userId);
        User user = (User) list.get(0);
        List<Group> groups = HibernateUtility.getGroupsForUser(user, session);
        user.setGroups(groups);
        return Response.status(Response.Status.FOUND).entity(user).build();
    }

    @POST
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postUser(@PathParam("id") String userId, User user) {
        if (!user.getUserId().equals(userId)) {
            return Response.status(Response.Status.CONFLICT).entity("UserId of URI and provider user do not match.").build();
        }
        Session session = HibernateUtility.getSessionFactory().openSession();
        if (HibernateUtility.userExists(userId, session)) {
            return Response.status(Response.Status.CONFLICT).entity("User already exists for UserId: " + userId).build();
        }
        session.beginTransaction();
        int id = (int) session.save(user);
        user.setId(id);
        user.setGroups(user.getGroups());
        HibernateUtility.persistGroupsForUser(user, session);
        session.getTransaction().commit();
        session.close();
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putUser(@PathParam("id") String userId, User putUser) {
        Session session = HibernateUtility.getSessionFactory().openSession();
        if (!HibernateUtility.userExists(userId, session)) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found for UserId: " + userId).build();
        }
        List<User> list = HibernateUtility.getUserListForId(session, userId);
        User user = (User) list.get(0);
        HibernateUtility.deleteGroupMappingsForUser(userId, session);
        user.setFirstName(putUser.getFirstName());
        user.setLastName(putUser.getLastName());
        user.setUserId(putUser.getUserId());
        user.setGroups(putUser.getGroups());
        session.beginTransaction();
        session.update(user);
        HibernateUtility.persistGroupsForUser(user, session);
        session.getTransaction().commit();
        session.close();
        return Response.status(Response.Status.OK).entity(user).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id") String userId) {
        Session session = HibernateUtility.getSessionFactory().openSession();
        if (!HibernateUtility.userExists(userId, session)) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found for UserId: " + userId).build();
        }
        HibernateUtility.deleteGroupMappingsForUser(userId, session);
        Query query = session.getNamedQuery(User.DELETE_USER_BY_USERID);
        query.setParameter(User.USER_ID_PARAMETER, userId);
        session.beginTransaction();
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
        return Response.status(Response.Status.OK).build();
    }


}
