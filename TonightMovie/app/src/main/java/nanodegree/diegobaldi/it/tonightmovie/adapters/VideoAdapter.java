package nanodegree.diegobaldi.it.tonightmovie.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.databinding.ItemVideoBinding;
import nanodegree.diegobaldi.it.tonightmovie.models.Video;
import nanodegree.diegobaldi.it.tonightmovie.viewmodels.VideoViewModel;

/**
 * Created by diego on 25/02/2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.BindingHolder> {
    private List<Video> mVideos;
    private Context mContext;

    public VideoAdapter(Context context) {
        mContext = context;
        mVideos = new ArrayList<>();
    }

    @Override
    public VideoAdapter.BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemVideoBinding itemVideoBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_video,
                parent,
                false);
        return new VideoAdapter.BindingHolder(itemVideoBinding);
    }

    @Override
    public void onBindViewHolder(VideoAdapter.BindingHolder holder, int position) {
        ItemVideoBinding itemVideoBinding = holder.binding;
        itemVideoBinding.setVideoViewModel(new VideoViewModel(mContext, mVideos.get(position)));
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    public void setItems(List<Video> videos) {
        mVideos = videos;
        notifyDataSetChanged();
    }

    public void addItem(Video video) {
        mVideos.add(video);
        notifyDataSetChanged();
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ItemVideoBinding binding;

        public BindingHolder(ItemVideoBinding binding) {
            super(binding.videoFrame);
            this.binding = binding;
        }
    }

}