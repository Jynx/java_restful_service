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
        name = "deleteGroupByName",
        query = "delete from rest_group rg where rg.group_name = :group_name",
        resultClass = Group.class),

    @NamedNativeQuery (
        name = "selectGroupByName",
        query = "select * from rest_group rg where rg.group_name = :group_name",
        resultClass = Group.class),

    @NamedNativeQuery (
        name = "deleteGroupUserMapping",
        query = "delete from rest_user_group_mapping rugp where rugp.group_name = :group_name",
        resultClass = Group.class),

    @NamedNativeQuery (
        name = "persistTestGroupUserMapping",
        query = "insert into rest_user_group_mapping values('999', 'testGroup', 'testUser', 00)",
        resultClass = Group.class),

    @NamedNativeQuery (
        name = "selectGroupUserMappingByName",
        query = "select * from rest_user_group_mapping rugp where rugp.group_name = :group_name",
        resultClass = Group.class)

})

@Entity
@Table(name = "rest_group")
public class Group {
    @Id
    @JsonIgnore
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "id")
    private int id;

    @JsonProperty("group")
    @Column(name ="group_name")
    private String groupName;

    public Group(){}

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
    public String toString(){
        return new StringBuffer("Group Name: "). append(this.groupName).toString();
    }
}
