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

    public static final String REST_USER_GROUP_MAPPING_TABLE = "rest_user_group_mapping";
    public static final String REST_GROUP_TABLE = "rest_group";
    public static final String REST_USER_TABLE = "rest_user";

    public static final String STATUS_OK = "ok";

    private static SessionFactory buildSessionFactory() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
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
        Query query = session.getNamedQuery(Group.SELECT_GROUP_BY_NAME);
        query.setParameter(Group.GROUP_NAME_PARAMETER, groupName);
        if (query.list().isEmpty()) {
            return false;
        }
        return true;
    }

    public static boolean userExists(String userId, Session session) {
        if (!getUserListForId(session, userId).isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean groupUserMappingExists(String groupName, Session session) {
        if (!getGroupUserMappingListForName(session, groupName).isEmpty()) {
            return true;
        }
        return false;
    }

    public static List<UserGroupMapping> getGroupUserMappingListForName(Session session, String groupName) {
        Query query = session.getNamedQuery(Group.SELECT_USER_GROUP_MAPPING_BY_NAME);
        query.setParameter(Group.GROUP_NAME_PARAMETER, groupName);
        return query.list();
    }

    public static List<Group> getGroupsForUser(User user, Session session) {
        Query query = session.getNamedQuery(User.GET_GROUPS_FOR_USER_FROM_MAPPING);
        query.setParameter(User.USER_ID_PARAMETER, user.getUserId());
        return query.list();
    }

    public static void persistGroupsForUser(User user, Session session) {
        int batchCount = 0;
        for (Group group : user.getGroups()) {
            if (!groupExists(group.getGroupName(), session)) {
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

    public static List<User> getUserListForId(Session session, String userId) {
        Query query = session.getNamedQuery(User.SELECT_USER_BY_USERID);
        query.setParameter(User.USER_ID_PARAMETER, userId);
        return query.list();
    }

    public static void deleteGroupMappingsForUser(String userId, Session session) {
        session.beginTransaction();
        Query query = session.getNamedQuery(User.DELETE_GROUP_USER_MAPPINGS_FOR_USER);
        query.setParameter(User.USER_ID_PARAMETER, userId);
        query.executeUpdate();
        session.getTransaction().commit();
    }

    public static String updateUserGroupMappingsForGroup(String groupName, List<String> userIds, Session session) {
        for (String userId : userIds) {
            if (!HibernateUtility.userExists(userId, session)) {
                return userId;
            }
        }
        session.beginTransaction();
        int batchCount = 0;
        Query query = null;
        for (String userId : userIds) {
            query = session.getNamedQuery(Group.CHECK_EXISTING_USER_GROUP_MAPPING_BY_ID_GROUP);
            query.setParameter(Group.GROUP_NAME_PARAMETER, groupName);
            query.setParameter(User.USER_ID_PARAMETER, userId);
            if (query.list().isEmpty()) {
                if (!HibernateUtility.groupExists(groupName, session)) {
                    Group newGroup = new Group(groupName);
                    session.save(newGroup);
                    batchCount++;
                }
                List<User> list = HibernateUtility.getUserListForId(session, userId);
                User user = (User) list.get(0);
                UserGroupMapping mapping = new UserGroupMapping(groupName, userId, user.getId());
                session.save(mapping);
                batchCount++;
            }
            if (batchCount % 20 == 0) {
                session.flush();
                session.clear();
            }
        }
        session.getTransaction().commit();
        return STATUS_OK;
    }


}