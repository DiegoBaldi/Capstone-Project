package nanodegree.diegobaldi.it.tonightmovie.models;

/**
 * Created by diego on 07/04/2017.
 */

public class LikeStatus {
    private int liked;
    private boolean isAccepted;

    public LikeStatus() {

    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(boolean accepted) {
        this.isAccepted = accepted;
    }
}
