package nanodegree.diegobaldi.it.tonightmovie.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

import nanodegree.diegobaldi.it.tonightmovie.TonightMovieApp;

/**
 * Created by diego on 26/02/2017.
 */

@IgnoreExtraProperties
public class MovieRequest implements Parcelable {
    private String id;
    private User author;
    private String description;
    private Movie movie;
    private String genre;
    private boolean completed = false;
    private Advice acceptedAdvice;
    private boolean isInterested;
    private long created;

    public MovieRequest(User author, String description, Movie movie, String genre){
        this.author = author;
        this.description = description;
        this.movie = movie;
        this.genre = genre;
    }

    public MovieRequest(){
    }

    public MovieRequest(User author, String shortDescription, String description, String genre){
        this.author = author;
        this.description = description;
        this.genre = genre;
    }

    public MovieRequest(Movie movie){
        this.author = TonightMovieApp.getUser();
        this.description = "";
        this.movie = new Movie(movie.getId(), movie.getOriginalTitle(), movie.getPosterKey());
    }

    protected MovieRequest(Parcel in) {
        id = in.readString();
        author = in.readParcelable(User.class.getClassLoader());
        description = in.readString();
        movie = in.readParcelable(Movie.class.getClassLoader());
        genre = in.readString();
        completed = in.readByte() != 0;
        created = in.readLong();
        isInterested = in.readByte() != 0;
    }

    public static final Creator<MovieRequest> CREATOR = new Creator<MovieRequest>() {
        @Override
        public MovieRequest createFromParcel(Parcel in) {
            return new MovieRequest(in);
        }

        @Override
        public MovieRequest[] newArray(int size) {
            return new MovieRequest[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Advice getAcceptedAdvice() {
        return acceptedAdvice;
    }

    public void setAcceptedAdvice(Advice acceptedAdvice) {
        this.acceptedAdvice = acceptedAdvice;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public boolean isInterested() {
        return isInterested;
    }

    public void setInterested(boolean interested) {
        isInterested = interested;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("author", author.toMap());
        result.put("description", description);
        result.put("movie", movie.toMap());
        result.put("genre", genre);
        result.put("completed", completed);
        result.put("created", ServerValue.TIMESTAMP);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(author, flags);
        dest.writeString(description);
        dest.writeParcelable(movie, flags);
        dest.writeString(genre);
        dest.writeByte((byte) (completed ? 1 : 0));
        dest.writeLong(created);
        dest.writeByte((byte) (isInterested ? 1 : 0));
    }
}
