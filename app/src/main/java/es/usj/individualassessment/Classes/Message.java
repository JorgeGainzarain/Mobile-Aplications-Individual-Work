package es.usj.individualassessment.Classes;
import java.util.Date;

public class Message {
    private User user;
    private String time;
    private String message;
    private String id;

    public Message() {
        this.user = new User("DefaultUser", "defaultPass");
        this.time = new Date().toString();
        this.message = "Default Message";
    }
    public Message(User user, String message, String time, String id) {
        this.user = user;
        this.time = time;
        this.message = message;
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
