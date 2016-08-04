package movieDB;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Ayush on 08-07-2016.
 */
public interface MovieDbAPI {
    String ENDPOINT = "http://api.themoviedb.org";

    @GET("/3/movie/{order}?")
    Call<MovieDB> getAPPID(
            @Path("order") String order,
            @Query("api_key") String api_key);
}
