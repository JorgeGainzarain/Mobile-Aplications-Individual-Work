package es.usj.individualassessment.Classes;

import java.io.Serializable;
import java.util.List;

import es.usj.individualassessment.Classes.City;

public class User implements Serializable {

    private static User instance;
    private String userName;
    private String userMail;
    private String password;

    public User(String userName, String userMail, String password){
        this.userName = userName;
        this.userMail = userMail;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
