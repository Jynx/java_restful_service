package com.svolocyk;

import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by seven on 7/19/2016.
 */
@Path("groups")
public class GroupResource {

    @GET
    @Path("{groupName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroup(@PathParam("groupName") String groupName) {
        return null;
    }


    @POST
    @Path("{groupName}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postGroup(@PathParam("groupName") String groupName) {
        Session session = HibernateUtility.getSessionFactory().openSession();
        if(groupExists(groupName, session)) {
            return Response.status(Response.Status.CONFLICT).entity("Group already exists for groupName: " + groupName).build();
        }
        Group group = new Group(groupName);
        session.beginTransaction();
        session.save(group);
        session.getTransaction().commit();
        session.close();
        return Response.status(Response.Status.CREATED).entity(group).build();
    }


    @DELETE
    @Path("{groupName}")
    public Response deleteGroupMembers(@PathParam("groupName") String groupName) {
        Session session = HibernateUtility.getSessionFactory().openSession();
        if(!groupUserMappingExists(groupName, session)) {
            return Response.status(Response.Status.NOT_FOUND).entity("Group User Mapping does not exist for groupName: " + groupName).build();
        }
        Query query = session.getNamedQuery("deleteGroupUserMapping");
        query.setParameter("group_name", groupName);
        session.beginTransaction();
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
        return Response.status(Response.Status.OK).build();
    }

    private boolean groupExists(String groupName, Session session) {
        if(!getGroupListForName(session, groupName).isEmpty()) {
            return true;
        }
        return false;
    }

    private List<?> getGroupListForName(Session session, String groupName) {
        Query query = session.getNamedQuery("selectGroupByName");
        query.setParameter("group_name", groupName);
        return query.list();
    }

    private boolean groupUserMappingExists(String groupName, Session session) {
        if(!getGroupUserMappingListForName(session, groupName).isEmpty()) {
            return true;
        }
        return false;
    }

    private List<?> getGroupUserMappingListForName(Session session, String groupName) {
        Query query = session.getNamedQuery("selectGroupUserMappingByName");
        query.setParameter("group_name", groupName);
        return query.list();
    }


}
