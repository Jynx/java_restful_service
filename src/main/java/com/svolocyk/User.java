package com.svolocyk;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Created by Steven Volocyk on 7/17/2016.
 */

@NamedNativeQueries({
    @NamedNativeQuery(
            name = "deleteUserByID",
            query = "delete from rest_user ru where ru.user_id = :user_id",
            resultClass = User.class ),

    @NamedNativeQuery (
            name = "selectUserByID",
            query = "select * from rest_user ru where ru.user_id = :user_id",
            resultClass = User.class )

})

@Entity
@Table(name = "rest_user")
public class User implements Serializable{
    @Column(name = "first_name")
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_id")
    private String userId;

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "id")
    private int id;

    public User(){}

    public User(String firstName, String lastName, String userId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;
    }

    public String getFirstName() {return firstName;
    }

    public void setFirstName(String firstName) { this.firstName = firstName;
    }

    public String getLastName() { return lastName;
    }

    public void setLastName(String lastName) { this.lastName = lastName;
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

    @Override
    public String toString(){
        return new StringBuffer("First Name: "). append(this.firstName).append("Last Name: ").append("UserID: ")
                .append(this.userId).toString();
    }
}
