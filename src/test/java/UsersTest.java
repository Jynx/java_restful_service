import com.svolocyk.HibernateUtility;
import com.svolocyk.User;
import com.svolocyk.UserResource;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import javax.ws.rs.core.Response;
import static junit.framework.Assert.assertEquals;

/**
 * Created by seven on 7/19/2016.
 */
public class UsersTest {
    private User testUser;
    private Session session;
    private UserResource userResource;

    @Before
    public void before() {
        session = HibernateUtility.getSessionFactory().openSession();
        userResource = new UserResource();
        testUser = new User("firstName", "lastName", "fLast");
    }

    @After
    public void after(){
        deleteTestUser();
    }

    @Test
    public void testGetNonExistentUser() {
        Response response =  userResource.getUser("ThisWontExistInDB");
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.NOT_FOUND.getReasonPhrase());
    }

    @Test
    public void testGetExistingUser() {
        persistTestUser();
        Response response =  userResource.getUser(testUser.getUserId());
        User retrievedUser = (User)response.getEntity();
        deleteTestUser();
        assertEquals(retrievedUser.getUserId(), testUser.getUserId());
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.FOUND.getReasonPhrase());
    }

    @Test
    public void testPostUserAlreadyExists() {
        persistTestUser();
        Response response =  userResource.postUser(testUser.getUserId(), testUser);
        deleteTestUser();
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.CONFLICT.getReasonPhrase());
    }

    @Test
    public void testPostUserMissMatchedUserID() {
        Response response =  userResource.postUser("IdThatWontMatchTestUser.getUserId", testUser);
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.CONFLICT.getReasonPhrase());
    }

    @Test
    public void testSuccessfulPostUser() {
        deleteTestUser();
        Response response =  userResource.postUser(testUser.getUserId(), testUser);
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.CREATED.getReasonPhrase());
    }

    @Test
    public void testPutUserDoesNotExist() {
        deleteTestUser();
        Response response =  userResource.putUser(testUser.getUserId(), testUser);
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.NOT_FOUND.getReasonPhrase());
    }

    @Test
    public void testPutUserExists() {
        persistTestUser();
        User changeUser = new User("firstNameChanged", "lastName", "fLast");
        Response response =  userResource.putUser(testUser.getUserId(), changeUser);
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.OK.getReasonPhrase());
        User returnedUser = (User)response.getEntity();
        assertEquals(changeUser.getFirstName(), returnedUser.getFirstName());
        deleteTestUser();
    }

    @Test
    public void testDeleteUserDoesNotExist() {
        deleteTestUser();
        Response response =  userResource.deleteUser(testUser.getUserId());
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.NOT_FOUND.getReasonPhrase());
    }

    @Test
    public void testDeleteUserExists() {
        persistTestUser();
        Response response =  userResource.deleteUser(testUser.getUserId());
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.OK.getReasonPhrase());
    }

    private void persistTestUser() {
        if(!session.isOpen()) {
            session = HibernateUtility.getSessionFactory().openSession();
        }
        session.beginTransaction();
        session.save(testUser);
        session.getTransaction().commit();
    }

    private void deleteTestUser() {
        if(!session.isOpen()) {
            session = HibernateUtility.getSessionFactory().openSession();
        }
        Query query = session.getNamedQuery("deleteUserByID");
        query.setParameter("user_id", testUser.getUserId());
        session.beginTransaction();
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }


}
