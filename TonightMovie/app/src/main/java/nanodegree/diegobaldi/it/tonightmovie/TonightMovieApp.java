package nanodegree.diegobaldi.it.tonightmovie;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import nanodegree.diegobaldi.it.tonightmovie.models.User;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by diego on 18/02/2017.
 */

public class TonightMovieApp extends Application {

    private static final String LOG_TAG = TonightMovieApp.class.getSimpleName();
    private Context sContext;
    private static User mUser;
    public static int mToDoReviewCount;
//    private ValueEventListener mEventListener, mConnectedEventListener;
//    private DatabaseReference mDatabase;
//    private DatabaseReference mConnectedRef;
    private static boolean activityInForeground;
    public static boolean userConnected;
    private static Retrofit mTheMovieDBRetrofit;

    public static final String BASE_URL = "https://api.themoviedb.org/3/";


    @Override
    public void onCreate() {
        super.onCreate();
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mToDoReviewCount = 0;
//        EventBus.getDefault().register(this);
//        Fabric.with(this, new Crashlytics());
        Fresco.initialize(this);
        sContext = this;

//        mUser = new User("kadksakdsak","Diego Baldi", "diego.baldi@hotmail.it", Uri.parse("https://www.google.it/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&cad=rja&uact=8&ved=0ahUKEwjSyoPnyJnSAhXIOxoKHfqLBy8QjRwIBw&url=https%3A%2F%2Fcoworkers.apps.cobot.me%2Fspaces%2Fspace-millepiani-coworking%2Fmemberships%2F19901&psig=AFQjCNFMpqHgh_BTGqjBE-ayT9JY4333Lw&ust=1487504647207168"));

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        mTheMovieDBRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();



//        mConnectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

//        FacebookSdk.sdkInitialize(this.getApplicationContext());
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //FirebaseDatabase.getInstance().getReference("services").keepSynced(true);
//        Dexter.initialize(sContext);

//        mEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists() && dataSnapshot.hasChildren()){
//                    mToDoReviewCount = Math.round(dataSnapshot.getChildrenCount());
//                    EventBus.getDefault().postSticky(new ToDoReviewsCountEvent(mToDoReviewCount));
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        };
//        ConnectivityChangeReceiver connectivityChangeReceiver = new ConnectivityChangeReceiver();
//        IntentFilter netFilter = new IntentFilter();
//        netFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        this.registerReceiver(connectivityChangeReceiver,  netFilter);
    }

    public Context getAppContext() {
        return sContext;
    }

    public static User getUser(){
        return mUser;
    }

    public static void clearUser() {
        mUser = null;
    }

    public static void setUser(User user){
        mUser = user;
    }

    public static int getToDoReviewCount() {
        return mToDoReviewCount;
    }

    public void addToDoReview() {
        this.mToDoReviewCount++;
    }

    public void removeToDoReview() {
        this.mToDoReviewCount++;
    }

    public static boolean isInForeground() {
        return activityInForeground;
    }

    public static void activityResumed() {
        activityInForeground = true;
    }

    public static void activityPaused() {
        activityInForeground = false;
    }

    public static Retrofit getRetrofit(){
        return mTheMovieDBRetrofit;
    }

//    @Subscribe(threadMode = ThreadMode.BACKGROUND)
//    public void onUserLogged(UserLoggedEvent event) {
//        mToDoReviewCount = 0;
//        mDatabase.child("todoReviews").child(event.uid).orderByChild("date").endAt(System.currentTimeMillis() - TWO_DAYS).addListenerForSingleValueEvent(mEventListener);
//
//    }
}
