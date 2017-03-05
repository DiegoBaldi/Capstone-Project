package nanodegree.diegobaldi.it.tonightmovie.api;

import nanodegree.diegobaldi.it.tonightmovie.models.ApiResult;
import nanodegree.diegobaldi.it.tonightmovie.models.Movie;
import nanodegree.diegobaldi.it.tonightmovie.models.MovieDetails;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by diego on 23/02/2017.
 */

public interface TheMovieDBApiEndpointInterface {

    @GET("movie/{movie_id}?append_to_response=videos,reviews")
    Call<MovieDetails> getMovieDetails(@Path("movie_id") int movieId, @Query("api_key") String apyKey, @Query("language") String language);

    @GET("movie/{movie_id}/reviews")
    Call<MovieDetails> getMovieReviews(@Path("movie_id") int movieId, @Query("api_key") String apyKey, @Query("language") String language, @Query("page") int page);

    @GET("movie/{movie_id}/videos")
    Call<MovieDetails> getMovieVideos(@Path("movie_id") int movieId, @Query("api_key") String apyKey, @Query("language") String language);

    @GET("search/movie")
    Call<ApiResult<Movie>> searchMovie(@Query("api_key") String apyKey, @Query("language") String language, @Query("query") String query, @Query("page") int page);

}
