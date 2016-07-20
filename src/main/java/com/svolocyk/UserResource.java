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
        if(!userExists(userId, session)) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found for UserId: " + userId).build();
        }
        List<?> list = getUserListForId(session, userId);
        User user = (User)list.get(0);
        //fix this
        List<Group> groups = getGroupsForUser(user, session);
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
        if(userExists(userId, session)) {
            return Response.status(Response.Status.CONFLICT).entity("User already exists for UserId: " + userId).build();
        }
        user.setUserId(userId);
        session.beginTransaction();
        int id = (int)session.save(user);
        user.setId(id);
        persistGroupsForUser(user, session);
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
        if(!userExists(userId, session)) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found for UserId: " + userId).build();
        }
        //clean this up? DONT DELETE GROUPS TIRED YOU
        List<?> list = getUserListForId(session, userId);
        User user = (User)list.get(0);
        deleteGroupMappingsForUser(user, session);

        user.setFirstName(putUser.getFirstName());
        user.setLastName(putUser.getLastName());
        user.setGroups(putUser.getGroups());
        session.beginTransaction();
        session.update(user);

        //fix this
        persistGroupsForUser(user, session);
        session.getTransaction().commit();
        session.close();
        return Response.status(Response.Status.OK).entity(user).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id") String userId) {
        Session session = HibernateUtility.getSessionFactory().openSession();
        if(!userExists(userId, session)) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found for UserId: " + userId).build();
        }
        Query query = session.getNamedQuery("deleteUserByID");
        query.setParameter("user_id", userId);
        session.beginTransaction();
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
        return Response.status(Response.Status.OK).build();
    }

    private List<Group> getGroupsForUser(User user, Session session) {
        Query query = session.getNamedQuery("getGroupsForUserFromMapping");
        query.setParameter("id", user.getId());
        return query.list();
    }

    private void persistGroupsForUser(User user, Session session) {
        for (Group group: user.getGroups()) {
            if(!groupExists(group.getGroupName(), session)) {
                Group newGroup = new Group(group.getGroupName());
                session.save(newGroup);
            }
            UserGroupMapping mapping = new UserGroupMapping(group.getGroupName(), user.getUserId(), user.getId());
            session.save(mapping);
        }
    }

    private boolean groupExists(String groupName, Session session) {
        Query query = session.getNamedQuery("selectGroupByName");
        query.setParameter("group_name", groupName);
        if(query.list().isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean userExists(String userId, Session session) {
        if(!getUserListForId(session, userId).isEmpty()) {
            return true;
        }
        return false;
    }

    private List<?> getUserListForId(Session session, String userId) {
        Query query = session.getNamedQuery("selectUserByID");
        query.setParameter("user_id", userId);
        return query.list();
    }

    private void deleteGroupMappingsForUser(User user, Session session) {
        session.beginTransaction();
        Query query = session.getNamedQuery("deleteGroupUserMappingsForUser");
        query.setParameter("id", user.getId());
        query.executeUpdate();
        session.getTransaction().commit();
    }

//    private void deleteGroupsForUser(User user, Session session) {
//        for(Group group : user.getGroups()) {
//            Query query = session.getNamedQuery("deleteGroupByName");
//            query.setParameter("group_name", group.getGroupName());
//            query.executeUpdate();
//            session.getTransaction().commit();
//        }
//    }

}
