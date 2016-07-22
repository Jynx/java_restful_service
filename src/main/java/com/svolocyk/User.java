/*
User contains all information and queries for manipulating User objects.
 */

package com.svolocyk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 * Created by Steven Volocyk on 7/17/2016.
 */

@NamedNativeQueries({
        @NamedNativeQuery(
                name = User.DELETE_USER_BY_USERID,
                query = "delete from " + HibernateUtility.REST_USER_TABLE + " ru where ru." +
                        User.USER_ID_COLUMN + "= :" + User.USER_ID_PARAMETER,
                resultClass = User.class),

        @NamedNativeQuery(
                name = User.SELECT_USER_BY_USERID,
                query = "select * from " + HibernateUtility.REST_USER_TABLE + " ru where ru." +
                        User.USER_ID_COLUMN + "= :" + User.USER_ID_PARAMETER,
                resultClass = User.class),

        @NamedNativeQuery(
                name = User.DELETE_GROUP_USER_MAPPINGS_FOR_USER,
                query = "delete from " + HibernateUtility.REST_USER_GROUP_MAPPING_TABLE +
                        " rugm where rugm." + User.USER_ID_COLUMN + "= :" + User.USER_ID_PARAMETER,
                resultClass = UserGroupMapping.class),

        @NamedNativeQuery(
                name = User.GET_GROUPS_FOR_USER_FROM_MAPPING,
                query = "SELECT * " +
                        "FROM "           + HibernateUtility.REST_GROUP_TABLE + " rg " +
                        "INNER JOIN "     + HibernateUtility.REST_USER_GROUP_MAPPING_TABLE + " rugm " +
                        "ON          rg." + Group.GROUP_NAME_COLUMN + " = rugm." + Group.GROUP_NAME_COLUMN +
                        " WHERE    rugm."  + User.USER_ID_COLUMN + " = :" + User.USER_ID_PARAMETER,
                resultClass = Group.class)
})

@Entity
@Table(name = HibernateUtility.REST_USER_TABLE)
public class User {

    public static final String DELETE_USER_BY_USERID = "deleteUserByUID";
    public static final String SELECT_USER_BY_USERID = "selectUserByUID";
    public static final String DELETE_GROUP_USER_MAPPINGS_FOR_USER = "deleteGroupUserMappingsForUser";
    public static final String GET_GROUPS_FOR_USER_FROM_MAPPING = "getGroupsForUserFromMapping";

    public static final String FIRST_NAME_COLUMN = "first_name";
    public static final String LAST_NAME_COLUMN = "last_name";
    public static final String USER_ID_COLUMN = "user_id";
    public static final String UNIQUE_ID_COLUMN = "id";

    public static final String GROUP_NAME_PARAMETER = "group_name";
    public static final String USER_ID_PARAMETER = "user_id";

    @JsonProperty("first_name")
    @Column(name = FIRST_NAME_COLUMN)
    private String firstName;

    @JsonProperty("last_name")
    @Column(name = LAST_NAME_COLUMN)
    private String lastName;

    @JsonProperty("userid")
    @Column(name = USER_ID_COLUMN)
    private String userId;

    @Id
    @JsonIgnore
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = UNIQUE_ID_COLUMN)
    private int id;

    @Transient
    @JsonProperty("groups")
    private List<Group> groups;

    public User() {
    }

    public User(String firstName, String lastName, String userId, List<Group> groups) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;
        this.groups = groups;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "First Name: " + this.firstName + " Last Name: " + this.lastName + " UserId: " + this.userId;
    }
}