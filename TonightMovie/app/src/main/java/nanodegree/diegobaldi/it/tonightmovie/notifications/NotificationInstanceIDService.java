package nanodegree.diegobaldi.it.tonightmovie.notifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import nanodegree.diegobaldi.it.tonightmovie.TonightMovieApp;
import nanodegree.diegobaldi.it.tonightmovie.models.User;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;

/**
 * Created by diego on 17/04/2017.
 */

public class NotificationInstanceIDService extends FirebaseInstanceIdService {
    private static final String LOG_TAG = NotificationInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(LOG_TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        if (TonightMovieApp.getUser() != null) {
            User user = TonightMovieApp.getUser();
            sendRegistrationToServer(refreshedToken, user.getId());
        }
        super.onTokenRefresh();
    }

    private void sendRegistrationToServer(String refreshedToken, String userId) {
        FirebaseUtil.getUsersRef().child(userId).child("fcmTokens").child(refreshedToken).setValue(true);
    }


}
