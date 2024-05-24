package es.usj.individualassessment.Classes;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import kotlin.random.Random;

import es.usj.individualassessment.Classes.City;

public class User implements Serializable {

    private static User instance;
    private String userName;
    private String userMail;

    private String color;

    public User() {
        this.userName = "Default User";
        this.userMail = "Default mail";
        this.color = String.format("#%06X", Random.Default.nextInt(0xFFFFFF + 1));
    }

    public User(FirebaseUser authUser) {
        this.userName = authUser.getDisplayName();
        this.userMail = authUser.getEmail();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = database.child("users").child(authUser.getUid());

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    color = dataSnapshot.child("color").getValue(String.class);
                } else {
                    color = String.format("#%06X", Random.Default.nextInt(0xFFFFFF + 1));
                }
            } else {
                // Handle the error
                Exception exception = task.getException();
                if (exception != null) {
                    Log.e("User", "Error fetching user data", exception);
                }
            }
        });
    }


    public User(String userName, String userMail){
        this.userName = userName;
        this.userMail = userMail;
        this.color = String.format("#%06X", Random.Default.nextInt(0xFFFFFF + 1));
    }

    public User(String userName, String userMail, String color) {
        this.userName = userName;
        this.userMail = userMail;
        this.color = color;
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


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}