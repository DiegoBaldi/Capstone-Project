package nanodegree.diegobaldi.it.tonightmovie.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import nanodegree.diegobaldi.it.tonightmovie.R;
import nanodegree.diegobaldi.it.tonightmovie.databinding.ClosedItemFeedBinding;
import nanodegree.diegobaldi.it.tonightmovie.databinding.PendingItemFeedBinding;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieRequest;
import nanodegree.diegobaldi.it.tonightmovie.viewmodels.ClosedRequestViewModel;
import nanodegree.diegobaldi.it.tonightmovie.viewmodels.PendingRequestViewModel;

/**
 * Created by diego on 28/02/2017.
 */

public class RequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MovieRequest> mRequests;
    private Context mContext;

    public RequestAdapter(Context context) {
        mContext = context;
        mRequests = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            PendingItemFeedBinding pendingItemFeedBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.pending_item_feed,
                    parent,
                    false);
            return new RequestAdapter.PendingBindingHolder(pendingItemFeedBinding);
        } else {
            ClosedItemFeedBinding closedItemFeedBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.closed_item_feed,
                    parent,
                    false);
            return new RequestAdapter.ClosedBindingHolder(closedItemFeedBinding);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == 1) {
            PendingItemFeedBinding pendingItemFeedBinding = ((PendingBindingHolder) holder).binding;
            pendingItemFeedBinding.setMovieRequestViewModel(new PendingRequestViewModel(mContext, mRequests.get(position)));
        } else {
            ClosedItemFeedBinding closedItemFeedBinding = ((ClosedBindingHolder) holder).binding;
            closedItemFeedBinding.setMovieRequestViewModel(new ClosedRequestViewModel(mContext, mRequests.get(position)));
        }

    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    public void setItems(List<MovieRequest> movieRequests) {
        mRequests = movieRequests;
        notifyDataSetChanged();
    }

    public void addItems(List<MovieRequest> request) {
//        int oldSize = mRequests.size()-1;
        mRequests.addAll(request);
//        notifyItemRangeChanged(oldSize, request.size()-1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        MovieRequest movieRequest = mRequests.get(position);
        if (movieRequest.getAcceptedAdvice() != null) {
            return 0;
        } else
            return 1;


    }

    public static class PendingBindingHolder extends RecyclerView.ViewHolder {
        private PendingItemFeedBinding binding;

        public PendingBindingHolder(PendingItemFeedBinding binding) {
            super(binding.pendingFrame);
            this.binding = binding;
        }
    }

    public static class ClosedBindingHolder extends RecyclerView.ViewHolder {
        private ClosedItemFeedBinding binding;

        public ClosedBindingHolder(ClosedItemFeedBinding binding) {
            super(binding.closedFrame);
            this.binding = binding;
        }
    }

}