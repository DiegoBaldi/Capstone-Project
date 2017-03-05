package nanodegree.diegobaldi.it.tonightmovie.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by diego on 26/02/2017.
 */

@IgnoreExtraProperties
public class Advice implements Parcelable{
    private String id;
    private Movie movie;
    private User author;
    private long date;
    private boolean isTheAnswer;
    private int karma;
    private int likeStatus;

    public Advice(){}

    protected Advice(Parcel in) {
        id = in.readString();
        movie = in.readParcelable(Movie.class.getClassLoader());
        author = in.readParcelable(User.class.getClassLoader());
        date = in.readLong();
        isTheAnswer = in.readByte() != 0;
        karma = in.readInt();
        likeStatus = in.readInt();
    }

    public static final Creator<Advice> CREATOR = new Creator<Advice>() {
        @Override
        public Advice createFromParcel(Parcel in) {
            return new Advice(in);
        }

        @Override
        public Advice[] newArray(int size) {
            return new Advice[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isTheAnswer() {
        return isTheAnswer;
    }

    public void setTheAnswer(boolean theAnswer) {
        isTheAnswer = theAnswer;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(int likeStatus) {
        this.likeStatus = likeStatus;
    }

    @Exclude
    public Map<String,Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("author", author.toMap());
        result.put("movie", movie.toMap());
        result.put("karma", karma);
        result.put("isTheAnswer", isTheAnswer);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(movie, flags);
        dest.writeParcelable(author, flags);
        dest.writeLong(date);
        dest.writeByte((byte) (isTheAnswer ? 1 : 0));
        dest.writeInt(karma);
        dest.writeInt(likeStatus);
    }
}
