package es.usj.individualassessment.Classes;

public class Message {
    private User user;
    private String time;
    private String message;

    public Message(User user, String message, String time) {
        this.user = user;
        this.time = time;
        this.message = message;
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
}
