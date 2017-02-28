package remotedoorway.byteme.com.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by Jay on 11/3/2016.
 */

@IgnoreExtraProperties
public class ChatAuthentication implements Serializable {
    private String UserID1;
    private String UserID2;
    private String Validate1;
    private String Validate2;
    private String Timestamp;
    private String ChatAuthenticationID;
    public ChatAuthentication() {
    }

    public ChatAuthentication(String userID1, String userID2, String validate1, String validate2, String timestamp) {
        UserID1 = userID1;
        UserID2 = userID2;
        Validate1 = validate1;
        Validate2 = validate2;
        Timestamp = timestamp;
    }

    public String getUserID1() {
        return UserID1;
    }

    public String getUserID2() {
        return UserID2;
    }

    public String getValidate1() {
        return Validate1;
    }

    public String getValidate2() {
        return Validate2;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public String getChatAuthenticationID() {
        return ChatAuthenticationID;
    }

    public void setChatAuthenticationID(String chatAuthenticationID) {
        ChatAuthenticationID = chatAuthenticationID;
    }

    @Override
    public String toString() {
        return "ChatAuthentication{" +
                "UserID1='" + UserID1 + '\'' +
                ", UserID2='" + UserID2 + '\'' +
                ", Validate1='" + Validate1 + '\'' +
                ", Validate2='" + Validate2 + '\'' +
                ", Timestamp='" + Timestamp + '\'' +
                '}';
    }
}

