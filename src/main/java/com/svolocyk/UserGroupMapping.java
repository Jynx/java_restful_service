/*
This class coresponds with the user_group_mapping table. This table is used to create a relationship between Users and Groups
where each row in the table corresponds with a group that a user belongs to. This class is designed to create objects corresponding
to these relationships.
 */

package com.svolocyk;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by seven on 7/20/2016.
 */
@Entity
@Table(name = HibernateUtility.REST_USER_GROUP_MAPPING_TABLE)
public class UserGroupMapping {

    public static final String GROUP_NAME_COLUMN = "group_name";
    public static final String USER_ID_COLUMN = "user_id";
    public static final String UNIQUE_USER_ID_COLUMN = "user_unique_id";
    public static final String ID_COLUMN = "id";

    @Column(name = GROUP_NAME_COLUMN)
    private String groupName;

    @Column(name = USER_ID_COLUMN)
    private String userId;

    @Column(name = UNIQUE_USER_ID_COLUMN)
    int userUniqueId;

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = ID_COLUMN)
    private int id;

    public UserGroupMapping() {
    }

    public UserGroupMapping(String groupName, String userId, int userUniqueId) {
        this.groupName = groupName;
        this.userId = userId;
        this.userUniqueId = userUniqueId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getUserUniqueId() {
        return userUniqueId;
    }

    public void setUserUniqueId(int userUniqueId) {
        this.userUniqueId = userUniqueId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}