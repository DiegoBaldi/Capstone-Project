package nanodegree.diegobaldi.it.tonightmovie.viewmodels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.net.Uri;

import nanodegree.diegobaldi.it.tonightmovie.TonightMovieApp;
import nanodegree.diegobaldi.it.tonightmovie.models.User;

/**
 * Created by diego on 12/04/2017.
 */

public class UserSettingsViewModel extends BaseObservable {

    private static final int SELECT_PICTURE_REQUEST_CODE = 124;
    private User user;
    private Context context;
    private boolean notifications;
    private Uri outputFileUri, resultUri, downloadUrl;

    public UserSettingsViewModel(User user, Context context, boolean notifications) {
        this.user = user;
        this.context = context;
        this.notifications = notifications;
    }

    public String getDisplayName() {
        return user.getDisplayName();
    }

    public String getBio() {
        return user.getBio();
    }

    public Boolean getNotifications(){
        return notifications;
    }

    public Uri getPhotoUrl(){
        if(user!=null)
            return Uri.parse(user.getPhotoURL());
        else
            return  Uri.parse(TonightMovieApp.getUser().getPhotoURL());
    }
}
