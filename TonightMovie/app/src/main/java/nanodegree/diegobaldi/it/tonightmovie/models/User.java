package nanodegree.diegobaldi.it.tonightmovie.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by diego on 18/02/2017.
 */

@IgnoreExtraProperties
public class User implements Parcelable {
    public String id;
    public String displayName;
    public String email;
    public String photoURL;

    public User(){}

    public User(String id, String displayName, String email, String photoURL) {
        this.id = id;
        this.displayName = displayName;
        this.email = email;
        this.photoURL = photoURL;
    }

    public User(String id, String displayName, String photoURL) {
        this.id = id;
        this.displayName = displayName;
        this.photoURL = photoURL;
    }

    protected User(Parcel in) {
        id = in.readString();
        displayName = in.readString();
        email = in.readString();
        photoURL = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("displayName", displayName);
        result.put("photoURL", photoURL);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(displayName);
        dest.writeString(email);
        dest.writeString(photoURL);
    }
}
