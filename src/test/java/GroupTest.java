import com.svolocyk.Group;
import com.svolocyk.GroupResource;
import com.svolocyk.HibernateUtility;
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
public class GroupTest {
    private Group testGroup;
    private Session session;
    private GroupResource groupResource;

    @Before
    public void before() {
        session = HibernateUtility.getSessionFactory().openSession();
        groupResource = new GroupResource();
        testGroup = new Group("testGroup");
    }

    @After
    public void after() {
        deleteTestGroup();
        deleteTestGroupUserMapping();
    }

    @Test
    public void testPostExistingGroup() {
        persistTestGroup();
        Response response =  groupResource.postGroup("testGroup");
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.CONFLICT.getReasonPhrase());
    }

    @Test
    public void testPostNewGroup() {
        deleteTestGroup();
        Response response =  groupResource.postGroup("testGroup");
        assertEquals(response.getStatusInfo().getReasonPhrase(), Response.Status.CREATED.getReasonPhrase());
    }

    @Test
    public void testDeleteExistingGroupMembers() {
        persistTestGroupUserMapping();
        Response response =  groupResource.deleteGroupMembers("testGroup");
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
        Query query = session.getNamedQuery("deleteGroupByName");
        query.setParameter("group_name", testGroup.getGroupName());
        session.beginTransaction();
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    private void persistTestGroupUserMapping() {
        if(!session.isOpen()) {
            session = HibernateUtility.getSessionFactory().openSession();
        }
        Query query = session.getNamedQuery("persistTestGroupUserMapping");
        session.beginTransaction();
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    private void deleteTestGroupUserMapping() {
        if(!session.isOpen()) {
            session = HibernateUtility.getSessionFactory().openSession();
        }
        Query query = session.getNamedQuery("deleteGroupUserMapping");
        query.setParameter("group_name", testGroup.getGroupName());
        session.beginTransaction();
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}
