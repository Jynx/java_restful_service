package com.svolocyk;

import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * Created by seven on 7/19/2016.
 */
@Path("groups")
public class GroupResource {

    @GET
    @Path("{groupName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroup(@PathParam("groupName") String groupName) {
        Session session = HibernateUtility.getSessionFactory().openSession();
        if(!HibernateUtility.groupExists(groupName, session)) {
            return Response.status(Response.Status.NOT_FOUND).entity("Requested Group Does Not Exist: " + groupName).build();
        }
        List<UserGroupMapping> userGroupMapping = HibernateUtility.getGroupUserMappingListForName(session, groupName);
        ArrayList<String> userIds = new ArrayList();
        for(UserGroupMapping mapping : userGroupMapping) {
            userIds.add(mapping.getUserId());
        }
        return Response.status(Response.Status.FOUND).entity(userIds).build();
    }

    @POST
    @Path("{groupName}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postGroup(@PathParam("groupName") String groupName) {
        Session session = HibernateUtility.getSessionFactory().openSession();
        if(HibernateUtility.groupExists(groupName, session)) {
            return Response.status(Response.Status.CONFLICT).entity("Group already exists for groupName: " + groupName).build();
        }
        Group group = new Group(groupName);
        session.beginTransaction();
        session.save(group);
        session.getTransaction().commit();
        session.close();
        return Response.status(Response.Status.CREATED).entity(group).build();
    }

    @PUT
    @Path("{groupName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateGroupMembers(@PathParam("groupName")String groupName, String members) {
        Session session = HibernateUtility.getSessionFactory().openSession();
        if(!HibernateUtility.groupExists(groupName, session)) {
            return Response.status(Response.Status.NOT_FOUND).entity("Requested Group Does Not Exist: " + groupName).build();
        }
        JsonParser jsonParser = new JsonParser();
        JsonObject jo = (JsonObject)jsonParser.parse(members);
        JsonArray jsonArr = jo.getAsJsonArray("members");
        Gson googleJson = new Gson();
        ArrayList memberList = googleJson.fromJson(jsonArr, ArrayList.class);
        String updateSuccess = HibernateUtility.updateUserGroupMappingsForGroup(groupName, memberList, session);
        if (!updateSuccess.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("User Does Not Exist: " + updateSuccess).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("{groupName}")
    public Response deleteGroupMembers(@PathParam("groupName") String groupName) {
        Session session = HibernateUtility.getSessionFactory().openSession();
        if(!HibernateUtility.groupUserMappingExists(groupName, session)) {
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




}
