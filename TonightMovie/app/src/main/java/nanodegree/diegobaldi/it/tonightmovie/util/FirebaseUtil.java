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
        if (user.getPhotoUrl() != null)
            return new User(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
        else
            return new User(user.getUid(), user.getDisplayName(), user.getEmail(), "customProfiel");
    }

    public static DatabaseReference getCurrentUserRef() {
        String uid = getCurrentUserId();
        if (uid != null) {
            return getBaseRef().child("users").child(getCurrentUserId());
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

    public static String getUsersPath() {
        return "users/";
    }

    public static String getUserPath(String userId) {
        return "users/" + userId;
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

    public static DatabaseReference getFavoritesRef(String profileId) {
        return getBaseRef().child("favorites").child(profileId);
    }

    public static DatabaseReference getRequestAdvicesRef() {
        return getBaseRef().child("requestAdvices");
    }

    public static DatabaseReference getRequestAdvicesMovieRef(String requestId, int adviceMovieId) {
        return getBaseRef().child("requestAdvices/" + requestId + "/" + adviceMovieId);
    }

    public static DatabaseReference getMovieAdvicesRef(int movieId) {
        return getBaseRef().child("movieAdvices/" + String.valueOf(movieId));
    }

    public static DatabaseReference getMovieAdvicesMovieRef(int movieId, int adviceMovieId) {
        return getBaseRef().child("movieAdvices/" + movieId + "/" + adviceMovieId);
    }

    public static String getMovieAdvicesPath(int movieId, int suggestedMovieId) {
        return "movieAdvices/" + movieId + "/" + suggestedMovieId;
    }

    public static String getRequestAdvicesPath(String requestId, int suggestedMovieId) {
        return "requestAdvices/" + requestId + "/" + suggestedMovieId;
    }

    public static String getGenreRequestsPath(String genre, String key) {
        return "genreRequests/" + genre + "/" + key;
    }

    public static String getRequestPath(String key) {
        return "requests/" + key;
    }

    public static DatabaseReference getUserRequestRef(String profileId) {
        return getBaseRef().child("userRequests/" + profileId);
    }

    public static String getUserRequestPath(String id, String key) {
        return "userRequests/" + id + "/" + key;
    }

    public static String getRequestUserPath(String id, String key) {
        return "requestUsers/" + key + "/" + id;
    }

    public static DatabaseReference getGenreRequestRef(String genre) {
        return getBaseRef().child("genreRequests/" + genre);
    }

    public static DatabaseReference getUserRequestAdvicesRef() {
        return getBaseRef().child("userRequestAdvices/" + getCurrentUserId());
    }

    public static DatabaseReference getUserMovieAdvicesRef() {
        return getBaseRef().child("userMovieAdvices/" + getCurrentUserId());
    }
}