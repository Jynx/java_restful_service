import com.svolocyk.Group;
import com.svolocyk.HibernateUtility;
import com.svolocyk.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by seven on 7/17/2016.
 */
public class HibernateTest {
    private SessionFactory sessionFactory;
    private Session session;
    private ArrayList<Group> groups;

    @Before
    public void before() {
        sessionFactory = HibernateUtility.getSessionFactory();
        assertNotNull(sessionFactory);
        session = sessionFactory.openSession();
        session.beginTransaction();
    }


    @Test
    public void returnsMatchingUsers() {
        User testUser =  new User("test1", "test2", "test3", groups);
        int userId1 = (int)session.save(testUser);
        testUser.setId(userId1);
        session.getTransaction().commit();

        session = sessionFactory.openSession();
        session.beginTransaction();

        User testUser1 = (User) session.load(User.class, userId1);
        assertEquals(testUser.getId(), testUser1.getId());
    }


}
