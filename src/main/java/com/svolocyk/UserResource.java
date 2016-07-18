package com.svolocyk;

import org.hibernate.Session;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
/**
 * Created by Steven Volocyk on 7/17/2016.
 */
@Path("users")
public class UserResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        Session session = HibernateUtility.getSessionFactory().openSession();
        session.beginTransaction();
        User user = new User("steve", "steve", "steve");
        Integer uID = (Integer) session.save(user);
        session.getTransaction().commit();
        session.close();
        return "Success";
    }

    @POST
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public User persistNewUser(@PathParam("id") String id) {
        return null;
    }
}
