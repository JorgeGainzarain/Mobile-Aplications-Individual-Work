package es.usj.individualassessment.Classes;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.List;

import es.usj.individualassessment.Classes.City;

public class User implements Serializable {

    private static User instance;
    private String userName;
    private String userMail;

    public User(FirebaseUser authUser) {
        this.userName = authUser.getDisplayName();
        this.userMail = authUser.getEmail();
    }
    public User(String userName, String userMail){
        this.userName = userName;
        this.userMail = userMail;
    }

    public static User getInstance() {
        return instance;
    }

    public static void setInstance(User instance) {
        User.instance = instance;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }


}
