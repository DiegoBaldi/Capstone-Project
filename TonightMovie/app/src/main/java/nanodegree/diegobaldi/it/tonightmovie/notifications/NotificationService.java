package nanodegree.diegobaldi.it.tonightmovie.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieRequest;
import nanodegree.diegobaldi.it.tonightmovie.util.FirebaseUtil;
import nanodegree.diegobaldi.it.tonightmovie.views.FeedActivity;
import nanodegree.diegobaldi.it.tonightmovie.views.RequestActivity;

/**
 * Created by diego on 26/04/2017.
 */

public class NotificationService extends FirebaseMessagingService {
    private static final String LOG_TAG = NotificationService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData()!=null && remoteMessage.getData().containsKey("title") && remoteMessage.getData().containsKey("message")
                && remoteMessage.getData().containsKey("requestId")){
            getRequestDetails(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"), remoteMessage.getData().get("requestId"), this);
        }
    }

    private void getRequestDetails(final String title, final String message, final String key, final Context context) {
        FirebaseUtil.getRequestRef().child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    MovieRequest request = dataSnapshot.getValue(MovieRequest.class);
                    request.setId(dataSnapshot.getKey());
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setDefaults(Notification.DEFAULT_SOUND)
                                    .setAutoCancel(true)
                                    .setSmallIcon(R.drawable.ic_notification)
                                    .setContentTitle(title)
                                    .setContentText(message);
                    // Creates an explicit intent for an Activity in your app
                    Intent resultIntent = new Intent(context, RequestActivity.class);
                    resultIntent.putExtra("request", request);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    // Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(FeedActivity.class);
                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    // mId allows you to update the notification later on.
                    mNotificationManager.notify(request.getMovie().getId(), mBuilder.build());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
