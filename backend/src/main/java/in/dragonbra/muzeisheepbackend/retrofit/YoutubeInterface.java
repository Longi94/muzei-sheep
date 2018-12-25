package in.dragonbra.muzeisheepbackend.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;

import in.dragonbra.muzeisheepbackend.retrofit.model.YoutubeResponse;
import retrofit2.http.Query;

/**
 * @author lngtr
 * @since 2018-12-25
 */
public interface YoutubeInterface {

    public static final String BASE_URL = "https://www.googleapis.com/youtube/v3/";

    @GET("playlistItems")
    public Call<YoutubeResponse> listPlaylistItems(
            @Query("playlistId") String id,
            @Query("part") String part,
            @Query("maxResults") Integer maxResults,
            @Query("pageToken") String pageToken
    );
}
