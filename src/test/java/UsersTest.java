import com.svolocyk.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * Created by seven on 7/19/2016.
 */
public class UsersTest {
    private User testUser;
    private Session session;
    private UserResource userResource;
    private ArrayList<Group> groups;

    @Before
    public void before() {
        session = HibernateUtility.getSessionFactory().openSession();
        userResource = new UserResource();
        Group group = new Group("testGroup");
        groups = new ArrayList();
        groups.add(group);
        testUser = new User("firstName", "lastName", "fLast", groups);
    }

    @After
    public void after() {
        deleteTestUser();
    }

    @Test
    public void testGetNonExistentUser() {
        Response response = userResource.getUser("ThisWontExistInDB");
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.NOT_FOUND.getReasonPhrase());
    }

    @Test
    public void testGetExistingUser() {
        persistTestUser();
        Response response = userResource.getUser(testUser.getUserId());
        User retrievedUser = (User) response.getEntity();
        deleteTestUser();
        assertEquals(retrievedUser.getUserId(), testUser.getUserId());
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.FOUND.getReasonPhrase());
    }

    @Test
    public void testPostUserAlreadyExists() {
        persistTestUser();
        Response response = userResource.postUser(testUser.getUserId(), testUser);
        deleteTestUser();
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.CONFLICT.getReasonPhrase());
    }

    @Test
    public void testPostUserMissMatchedUserID() {
        Response response = userResource.postUser("IdThatWontMatchTestUser.getUserId", testUser);
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.CONFLICT.getReasonPhrase());
    }

    @Test
    public void testSuccessfulPostUser() {
        deleteTestUser();
        Response response = userResource.postUser(testUser.getUserId(), testUser);
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.CREATED.getReasonPhrase());
    }

    @Test
    public void testPutUserDoesNotExist() {
        deleteTestUser();
        Response response = userResource.putUser(testUser.getUserId(), testUser);
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.NOT_FOUND.getReasonPhrase());
    }

    @Test
    public void testPutUserExists() {
        persistTestUser();
        User changeUser = new User("firstNameChanged", "lastName", "fLast", groups);
        Response response = userResource.putUser(testUser.getUserId(), changeUser);
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.OK.getReasonPhrase());
        User returnedUser = (User) response.getEntity();
        assertEquals(changeUser.getFirstName(), returnedUser.getFirstName());
        deleteTestUser();
    }

    @Test
    public void testDeleteUserDoesNotExist() {
        deleteTestUser();
        Response response = userResource.deleteUser(testUser.getUserId());
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.NOT_FOUND.getReasonPhrase());
    }

    @Test
    public void testDeleteUserExists() {
        persistTestUser();
        Response response = userResource.deleteUser(testUser.getUserId());
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.OK.getReasonPhrase());
        deleteTestUser();
    }

    private void persistTestUser() {
        if (!session.isOpen()) {
            session = HibernateUtility.getSessionFactory().openSession();
        }
        session.beginTransaction();
        int id = (int) session.save(testUser);
        testUser.setId(id);
        persistGroupsForUser(testUser, session);
        session.getTransaction().commit();
    }

    private void persistGroupsForUser(User user, Session session) {
        int batchCount = 0;
        for (Group group : user.getGroups()) {
            if (!HibernateUtility.groupExists(group.getGroupName(), session)) {
                Group newGroup = new Group(group.getGroupName());
                session.save(newGroup);
            }
            UserGroupMapping mapping = new UserGroupMapping(group.getGroupName(), user.getUserId(), user.getId());
            session.save(mapping);
            batchCount++;
            if (batchCount % 20 == 0) {
                session.flush();
                session.clear();
            }
        }
    }

    private void deleteTestUser() {
        if (!session.isOpen()) {
            session = HibernateUtility.getSessionFactory().openSession();
        }
        session.beginTransaction();
        deleteUser();
        deleteTestGroupUserMappingsForUser();
        deleteTestGroupsForUser();
        session.getTransaction().commit();
    }


    private void deleteUser() {
        Query query = session.getNamedQuery(User.DELETE_USER_BY_USERID);
        query.setParameter(User.USER_ID_PARAMETER, testUser.getUserId());
        query.executeUpdate();
    }

    private void deleteTestGroupUserMappingsForUser() {
        Query query = session.getNamedQuery(User.DELETE_GROUP_USER_MAPPINGS_FOR_USER);
        query.setParameter(User.USER_ID_PARAMETER, testUser.getUserId());
        query.executeUpdate();
    }

    private void deleteTestGroupsForUser() {
        int batchCount = 0;
        for (Group group : testUser.getGroups()) {
            Query query = session.getNamedQuery(Group.DELETE_GROUP_BY_NAME);
            query.setParameter(Group.GROUP_NAME_PARAMETER, group.getGroupName());
            query.executeUpdate();
            batchCount++;
            if (batchCount % 20 == 0) {
                session.flush();
                session.clear();
            }
        }
    }
}