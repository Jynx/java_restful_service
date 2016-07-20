package com.svolocyk;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by seven on 7/20/2016.
 */
@Entity
@Table(name = "rest_user_group_mapping")
public class UserGroupMapping {

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_unique_id")
    int userUniqueId;

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "id")
    private int id;

    public UserGroupMapping(){}

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
