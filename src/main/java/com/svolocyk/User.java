package com.svolocyk;

import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Created by Steven Volocyk on 7/17/2016.
 */

@Entity
@Table(name = "rest_user")
public class User implements Serializable{
    @Column(name = "first_name")
    private String first_name;

    @Column(name = "last_name")
    private String last_name;

    @Column(name = "user_id")
    private String userId;

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "id")
    private int id;

    public User(){}

    public User(String first_name, String last_name, String userId) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.userId = userId;
    }

    public String get_first_name() {
        return this.first_name;
    }

    public void set_first_name(String first_name) {
        this.first_name = first_name;
    }

    public String get_last_name() {
        return this.last_name;
    }

    public void set_last_name(String last_name) {
        this.last_name = last_name;
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
}
