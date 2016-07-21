import com.svolocyk.Group;
import com.svolocyk.GroupResource;
import com.svolocyk.HibernateUtility;
import com.svolocyk.User;
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
public class GroupTest {
    private Group testGroup;
    private Session session;
    private GroupResource groupResource;
    private String testUsers;
    private ArrayList<Group> userGroupList = new ArrayList<>();
    User user1;
    User user2;

    @Before
    public void before() {
        session = HibernateUtility.getSessionFactory().openSession();
        groupResource = new GroupResource();
        testGroup = new Group("testGroup");
        userGroupList.add(testGroup);
        testUsers = "{\"members\": [\"one\", \"two\"]}";
        user1 = new User("test", "test", "one", userGroupList);
        user2 = new User("test", "test", "two", userGroupList);
    }

    @After
    public void after() {
        deleteTestGroup();
        deleteTestGroupUserMapping();
    }

    @Test
    public void testGetNonExistingGroup() {
        Response response =  groupResource.getGroup("testGroup");
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.NOT_FOUND.getReasonPhrase());
    }

    @Test
    public void testGetExistingGroup() {
        persistTestGroup();
        persistTestGroupUserMapping();
        Response response =  groupResource.getGroup("testGroup");
        deleteTestGroup();
        deleteTestGroupUserMapping();
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.FOUND.getReasonPhrase());
    }

    @Test
    public void testPostExistingGroup() {
        persistTestGroup();
        Response response =  groupResource.postGroup("testGroup");
        deleteTestGroup();
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.CONFLICT.getReasonPhrase());
    }

    @Test
    public void testPostNewGroup() {
        deleteTestGroup();
        Response response =  groupResource.postGroup("testGroup");
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.CREATED.getReasonPhrase());
    }

    @Test
    public void testPutNonExistingGroup() {
        Response response =  groupResource.updateGroupMembers("testGroup", testUsers);
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.NOT_FOUND.getReasonPhrase());
    }

    @Test
    public void testPutExistingGroupMissingUser() {
        persistTestGroup();
        Response response =  groupResource.updateGroupMembers("testGroup", testUsers);
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.NOT_FOUND.getReasonPhrase());
    }

    @Test
    public void testPutExistingGroupWithExistingUser() {
        persistTestGroup();
        persistTestUsers();
        Response response =  groupResource.updateGroupMembers("testGroup", testUsers);
        deleteTestUsers();
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.OK.getReasonPhrase());
    }

    @Test
    public void testDeleteExistingGroupMembers() {
        persistTestGroupUserMapping();
        Response response =  groupResource.deleteGroupMembers("testGroup");
        deleteTestGroupUserMapping();
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.OK.getReasonPhrase());
    }

    @Test
    public void testDeleteNonExistingGroupMembers() {
        deleteTestGroupUserMapping();
        Response response =  groupResource.deleteGroupMembers("testGroup");
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.NOT_FOUND.getReasonPhrase());
    }

    private void persistTestGroup() {
        if(!session.isOpen()) {
            session = HibernateUtility.getSessionFactory().openSession();
        }
        session.beginTransaction();
        session.save(testGroup);
        session.getTransaction().commit();
    }

    private void deleteTestGroup() {
        if(!session.isOpen()) {
            session = HibernateUtility.getSessionFactory().openSession();
        }
        Query query = session.getNamedQuery(Group.DELETE_GROUP_BY_NAME);
        query.setParameter(Group.GROUP_NAME_PARAMETER, testGroup.getGroupName());
        session.beginTransaction();
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    private void persistTestGroupUserMapping() {
        if(!session.isOpen()) {
            session = HibernateUtility.getSessionFactory().openSession();
        }
        Query query = session.getNamedQuery(Group.PERSIST_TEST_USER_GROUP_MAPPING);
        session.beginTransaction();
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    private void deleteTestGroupUserMapping() {
        if(!session.isOpen()) {
            session = HibernateUtility.getSessionFactory().openSession();
        }
        Query query = session.getNamedQuery(Group.DELETE_USER_GROUP_MAPPING);
        query.setParameter(Group.GROUP_NAME_PARAMETER, testGroup.getGroupName());
        session.beginTransaction();
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    private void persistTestUsers() {
        if(!session.isOpen()) {
            session = HibernateUtility.getSessionFactory().openSession();
        }
        session.beginTransaction();
        int id = (int)session.save(user1);
        int id1 = (int)session.save(user2);
        user1.setId(id);
        user2.setId(id1);
        session.getTransaction().commit();
    }

    private void deleteTestUsers() {
        if (!session.isOpen()) {
            session = HibernateUtility.getSessionFactory().openSession();
        }
        session.beginTransaction();
        deleteUsers(user1.getUserId());
        deleteUsers(user2.getUserId());
        session.getTransaction().commit();
    }

    private void deleteUsers(String userId) {
        Query query = session.getNamedQuery(User.DELETE_USER_BY_USERID);
        query.setParameter(User.USER_ID_PARAMETER, userId);
        query.executeUpdate();
    }
}