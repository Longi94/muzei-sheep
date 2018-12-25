package in.dragonbra.muzeisheepbackend.config;

import in.dragonbra.muzeisheepbackend.interceptor.QueryParamInterceptor;
import in.dragonbra.muzeisheepbackend.retrofit.YoutubeInterface;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author lngtr
 * @since 2018-12-25
 */
@Configuration
public class YoutubeConfig {

    @Value("${google.auth.api-key}")
    private String googleApiKey;

    @Bean
    public YoutubeInterface youtubeInterface() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new QueryParamInterceptor("key", googleApiKey))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YoutubeInterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit.create(YoutubeInterface.class);
    }
}
