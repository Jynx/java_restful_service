package com.svolocyk;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Created by Steven Volocyk on 7/17/2016.
 */
public class HibernateUtility {

    private static SessionFactory sessionFactory;

    private static SessionFactory buildSessionFactory() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
        return sessionFactory;
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            return buildSessionFactory();
        }
        return sessionFactory;
    }

    public static boolean groupExists(String groupName, Session session) {
        Query query = session.getNamedQuery("selectGroupByName");
        query.setParameter("group_name", groupName);
        if(query.list().isEmpty()) {
            return false;
        }
        return true;
    }

    public static boolean userExists(String userId, Session session) {
        if(!getUserListForId(session, userId).isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean groupUserMappingExists(String groupName, Session session) {
        if(!getGroupUserMappingListForName(session, groupName).isEmpty()) {
            return true;
        }
        return false;
    }

    public static List<UserGroupMapping> getGroupUserMappingListForName(Session session, String groupName) {
        Query query = session.getNamedQuery("selectGroupUserMappingByName");
        query.setParameter("group_name", groupName);
        return query.list();
    }

    public static List<Group> getGroupsForUser(User user, Session session) {
        Query query = session.getNamedQuery("getGroupsForUserFromMapping");
        query.setParameter("id", user.getUserId());
        return query.list();
    }

    public static void persistGroupsForUser(User user, Session session) {
        int batchCount = 0;
        for (Group group: user.getGroups()) {
            if(!groupExists(group.getGroupName(), session)) {
                Group newGroup = new Group(group.getGroupName());
                session.save(newGroup);
            }
            UserGroupMapping mapping = new UserGroupMapping(group.getGroupName(), user.getUserId(), user.getId());
            session.save(mapping);
            batchCount++;
            if(batchCount % 20 == 0) {
                session.flush();
                session.clear();
            }
        }
    }

    public static List<User> getUserListForId(Session session, String userId) {
        Query query = session.getNamedQuery("selectUserByID");
        query.setParameter("user_id", userId);
        return query.list();
    }

    public static void deleteGroupMappingsForUser(String userId, Session session) {
        session.beginTransaction();
        Query query = session.getNamedQuery("deleteGroupUserMappingsForUser");
        query.setParameter("id", userId);
        query.executeUpdate();
        session.getTransaction().commit();
    }

    public static String updateUserGroupMappingsForGroup(String groupName, List<String> userIds, Session session) {
        for(String userId : userIds) {
            if (!HibernateUtility.userExists(userId, session)) {
                return userId;
            }
        }
        session.beginTransaction();
        int batchCount = 0;
        Query query = null;
        for (String userId : userIds)  {
            query = session.getNamedQuery("checkForExistingUserAndGroupMappingByUserIdAndGroupName");
            query.setParameter("group_name", groupName);
            query.setParameter("user_id", userId);
            if (query.list().isEmpty()) {
                if(!HibernateUtility.groupExists(groupName, session)) {
                    Group newGroup = new Group(groupName);
                    session.save(newGroup);
                    batchCount++;
                }
                List<User> list = HibernateUtility.getUserListForId(session, userId);
                User user = (User)list.get(0);
                UserGroupMapping mapping = new UserGroupMapping(groupName, userId, user.getId());
                session.save(mapping);
                batchCount++;
            }
            if(batchCount % 20 == 0) {
                session.flush();
                session.clear();
            }
        }
        session.getTransaction().commit();
        return "";
    }


}
