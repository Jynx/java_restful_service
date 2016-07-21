package com.svolocyk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by seven on 7/19/2016.
 */

@NamedNativeQueries({
        @NamedNativeQuery(
                name = Group.DELETE_GROUP_BY_NAME,
                query = "delete from " + HibernateUtility.REST_GROUP_TABLE
                        + " rg where rg." + Group.GROUP_NAME_COLUMN + " = :" + Group.GROUP_NAME_PARAMETER,
                resultClass = Group.class),

        @NamedNativeQuery(
                name = Group.SELECT_GROUP_BY_NAME,
                query = "select * from " + HibernateUtility.REST_GROUP_TABLE + " rg where rg."
                        + Group.GROUP_NAME_COLUMN + " = :" + Group.GROUP_NAME_PARAMETER,
                resultClass = Group.class),

        @NamedNativeQuery(
                name = Group.DELETE_USER_GROUP_MAPPING,
                query = "delete from " + HibernateUtility.REST_USER_GROUP_MAPPING_TABLE
                        + " rugp where rugp." + Group.GROUP_NAME_COLUMN + " = :" + Group.GROUP_NAME_PARAMETER,
                resultClass = UserGroupMapping.class),

        @NamedNativeQuery(
                name = Group.PERSIST_TEST_USER_GROUP_MAPPING,
                query = "insert into " + HibernateUtility.REST_USER_GROUP_MAPPING_TABLE
                        + " values('999', 'testGroup', 'testUser', 00)",
                resultClass = UserGroupMapping.class),

        @NamedNativeQuery(
                name = Group.SELECT_USER_GROUP_MAPPING_BY_NAME,
                query = "select * from " + HibernateUtility.REST_USER_GROUP_MAPPING_TABLE
                        + " rugp where rugp." + Group.GROUP_NAME_COLUMN + " = :" + Group.GROUP_NAME_PARAMETER,
                resultClass = UserGroupMapping.class),

        @NamedNativeQuery(
                name = Group.CHECK_EXISTING_USER_GROUP_MAPPING_BY_ID_GROUP,
                query = "select * from " + HibernateUtility.REST_USER_GROUP_MAPPING_TABLE + " rugp where rugp."
                        + Group.GROUP_NAME_COLUMN + " = :" + Group.GROUP_NAME_PARAMETER + " and rugp." + Group.USER_ID_COLUMN + " = :" + Group.USER_ID_PARAMETER,
                resultClass = UserGroupMapping.class)

})

@Entity
@Table(name = HibernateUtility.REST_GROUP_TABLE)
public class Group {

    public static final String DELETE_GROUP_BY_NAME = "deleteGroupByName";
    public static final String SELECT_GROUP_BY_NAME = "selectGroupByName";
    public static final String DELETE_USER_GROUP_MAPPING = "deleteGroupUserMapping";
    public static final String PERSIST_TEST_USER_GROUP_MAPPING = "persistTestGroupUserMapping";
    public static final String SELECT_USER_GROUP_MAPPING_BY_NAME = "selectUserGroupMappingByName";
    public static final String CHECK_EXISTING_USER_GROUP_MAPPING_BY_ID_GROUP = "checkExistingUserGroupMappingByIdGroup";

    public static final String GROUP_NAME_COLUMN = "group_name";
    public static final String USER_ID_COLUMN = "user_id";
    public static final String UNIQUE_GROUP_ID_COLUMN = "id";

    public static final String GROUP_NAME_PARAMETER = "group_name";
    public static final String USER_ID_PARAMETER = "user_id";

    @Id
    @JsonIgnore
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = UNIQUE_GROUP_ID_COLUMN)
    private int id;

    @JsonProperty("group")
    @Column(name = GROUP_NAME_COLUMN)
    private String groupName;

    public Group() {
    }

    public Group(String groupName) {
        this.groupName = groupName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "Group Name: " + this.groupName;
    }
}