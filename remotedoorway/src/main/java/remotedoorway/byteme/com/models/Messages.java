package remotedoorway.byteme.com.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by Jay on 11/4/2016.
 */

@IgnoreExtraProperties
public class Messages implements Serializable {
    private String Content;
    private String UserId;
    private String Timestamp;

    public Messages() {
    }

    public Messages(String content, String userId, String timestamp) {
        Content = content;
        UserId = userId;
        Timestamp = timestamp;
    }

    public String getContent() {
        return Content;
    }

    public String getUserId() {
        return UserId;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    @Override
    public String toString() {
        return "Messages{" +
                "Content='" + Content + '\'' +
                ", UserId='" + UserId + '\'' +
                ", Timestamp='" + Timestamp + '\'' +
                '}';
    }
}
