package nanodegree.diegobaldi.it.tonightmovie.util;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import nanodegree.diegobaldi.it.tonightmovie.models.User;

/**
 * Created by diego on 21/04/2017.
 */

public class PointService extends IntentService {

    private static final String LOG_TAG = PointService.class.getSimpleName();

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public PointService() {
        super("PointService");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        String authorId = intent.getStringExtra("authorId");
        String type = intent.getStringExtra("type");
        final String genre = intent.getStringExtra("genre");
        int tempVar = 0;
        switch (type){
            case "upVote":
                tempVar++;
                break;
            case "downVote":
                tempVar--;
                break;
            case "accepted":
                tempVar += 50;
                break;
            case "notAccepted":
                tempVar -= 50;
                break;
        }
        final int variation = tempVar;
        FirebaseUtil.getUsersRef().child(authorId).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User mutableUser = mutableData.getValue(User.class);
                if (mutableUser == null) {
                    return Transaction.success(mutableData);
                }

                int exp = 0;

                if (genre.equalsIgnoreCase("Action") || genre.equalsIgnoreCase("Adventure"))
                    exp = mutableUser.getReckless();
                else if (genre.equalsIgnoreCase("Animation") || genre.equalsIgnoreCase("Comedy"))
                    exp = mutableUser.getHilarious();
                else if (genre.equalsIgnoreCase("Crime") || genre.equalsIgnoreCase("Horror") || genre.equalsIgnoreCase("Thriller"))
                    exp = mutableUser.getFearless();
                else if (genre.equalsIgnoreCase("Documentary") || genre.equalsIgnoreCase("Science fiction"))
                    exp = mutableUser.getNerd();
                else if (genre.equalsIgnoreCase("Romance") || genre.equalsIgnoreCase("Family"))
                    exp = mutableUser.getOverprotective();
                else if (genre.equalsIgnoreCase("Drama"))
                    exp = mutableUser.getEmpathic();

                exp += variation;

                if (genre.equalsIgnoreCase("Action") || genre.equalsIgnoreCase("Adventure"))
                    mutableUser.setReckless(exp);
                else if (genre.equalsIgnoreCase("Animation") || genre.equalsIgnoreCase("Comedy"))
                    mutableUser.setHilarious(exp);
                else if (genre.equalsIgnoreCase("Crime") || genre.equalsIgnoreCase("Horror") || genre.equalsIgnoreCase("Thriller"))
                    mutableUser.setFearless(exp);
                else if (genre.equalsIgnoreCase("Documentary") || genre.equalsIgnoreCase("Science fiction"))
                    mutableUser.setNerd(exp);
                else if (genre.equalsIgnoreCase("Romance") || genre.equalsIgnoreCase("Family"))
                    mutableUser.setOverprotective(exp);
                else if (genre.equalsIgnoreCase("Drama"))
                    mutableUser.setEmpathic(exp);

                // Set value and report transaction success
                mutableData.setValue(mutableUser);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(LOG_TAG, "adviceTransaction:onComplete:" + databaseError);
            }
        });
    }
}