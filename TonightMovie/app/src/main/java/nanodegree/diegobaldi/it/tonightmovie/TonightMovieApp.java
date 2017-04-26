package nanodegree.diegobaldi.it.tonightmovie;

import android.app.Application;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import nanodegree.diegobaldi.it.tonightmovie.models.User;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by diego on 18/02/2017.
 */

public class TonightMovieApp extends Application {

    private static final String LOG_TAG = TonightMovieApp.class.getSimpleName();
    private static User mUser;
    public static final double KONSTANT_LEVEL = 0.2582;
    private static Retrofit mTheMovieDBRetrofit;

    public static final String BASE_URL = "https://api.themoviedb.org/3/";


    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null && TonightMovieApp.getUser()==null){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user.getPhotoUrl()!=null)
                setUser(new User(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString()));
            else
                setUser(new User(user.getUid(), user.getDisplayName(), user.getEmail()));
        }


        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        mTheMovieDBRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    public static User getUser(){
        return mUser;
    }

    public static void clearUser() {
        mUser = null;
    }

    public static void setUser(User user){
        mUser = user;
        getUserFromDB();
    }

    public static Retrofit getRetrofit(){
        return mTheMovieDBRetrofit;
    }

    private static void getUserFromDB() {
        FirebaseUtil.getCurrentUserRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    saveUserToDb(TonightMovieApp.getUser());
                } else {
                    mUser = dataSnapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void saveUserToDb(User user) {
        Map<String, Object> userValues = user.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(FirebaseUtil.getUserPath(user.getId()), userValues);
        FirebaseUtil.getBaseRef().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.d(LOG_TAG, "arrived");
            }
        });
    }




}
