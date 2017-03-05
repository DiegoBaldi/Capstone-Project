package nanodegree.diegobaldi.it.tonightmovie.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.net.Uri;
import android.view.View;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.models.Video;

/**
 * Created by diego on 25/02/2017.
 */

public class VideoViewModel  extends BaseObservable {

    private Context context;
    private Video video;

    public VideoViewModel(Context context, Video video) {
        this.context = context;
        this.video = video;
    }

    public Uri getThumb(){
        return Uri.parse(String.format(context.getString(R.string.trailer_thumb_url), video.getKey()));
    }

    public String getName(){
        return video.getName();
    }

    public View.OnClickListener onClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchVideoActivity();
            }
        };
    }

    private void launchVideoActivity() {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(context.getString(R.string.trailer_link), video.getKey()))));
    }

}
