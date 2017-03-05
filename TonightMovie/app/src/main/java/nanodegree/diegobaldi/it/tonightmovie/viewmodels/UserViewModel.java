package nanodegree.diegobaldi.it.tonightmovie.viewmodels;

import android.databinding.BaseObservable;
import android.net.Uri;

import nanodegree.diegobaldi.it.tonightmovie.models.User;

/**
 * Created by diego on 26/02/2017.
 */

public class UserViewModel extends BaseObservable {

    private User user;

    public UserViewModel(User user) {
        this.user = user;
    }

    public String getDisplayName() {
        return user.getDisplayName();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public Uri getPhotoUrl(){
        return Uri.parse(user.getPhotoURL());
    }
}
