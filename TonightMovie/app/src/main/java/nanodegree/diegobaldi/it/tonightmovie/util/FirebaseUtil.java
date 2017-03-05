package nanodegree.diegobaldi.it.tonightmovie.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import nanodegree.diegobaldi.it.tonightmovie.models.User;

/**
 * Created by diego on 26/02/2017.
 */

public class FirebaseUtil {
    public static DatabaseReference getBaseRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

    public static User getUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return null;
        return new User(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
    }

    public static DatabaseReference getCurrentUserRef() {
        String uid = getCurrentUserId();
        if (uid != null) {
            return getBaseRef().child("people").child(getCurrentUserId());
        }
        return null;
    }

    public static DatabaseReference getPostsRef() {
        return getBaseRef().child("posts");
    }

    public static String getPostsPath() {
        return "posts/";
    }

    public static DatabaseReference getUsersRef() {
        return getBaseRef().child("users");
    }

    public static String getPeoplePath() {
        return "people/";
    }

    public static DatabaseReference getRequestRef() {
        return getBaseRef().child("requests");
    }

    public static DatabaseReference getCommentsRef() {
        return getBaseRef().child("comments");
    }

    public static DatabaseReference getFeedRef() {
        return getBaseRef().child("feed");
    }

    public static DatabaseReference getLikesRef() {
        return getBaseRef().child("likes");
    }

    public static DatabaseReference getFollowersRef() {
        return getBaseRef().child("followers");
    }

    public static DatabaseReference getFavoritesRef() {
        return getBaseRef().child("favorites");
    }

    public static DatabaseReference getRequestAdvicesRef() {
        return getBaseRef().child("requestAdvices");
    }

    public static DatabaseReference getMovieAdvicesRef(int movieId) {
        return getBaseRef().child("movieAdvices/"+String.valueOf(movieId));
    }

    public static String getMovieAdvicesPath(int movieId, int suggestedMovieid) {
        return "movieAdvices/"+String.valueOf(movieId)+"/"+String.valueOf(suggestedMovieid);
    }

    public static String getRequestAdvicesPath(String requestId, String key) {
        return "requestAdvices/"+requestId+"/"+key;
    }
}