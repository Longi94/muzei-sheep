package in.dragonbra.muzeisheepbackend.interceptor;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author lngtr
 * @since 2018-12-25
 */
public class QueryParamInterceptor implements Interceptor {

    private final String key;

    private final String value;

    public QueryParamInterceptor(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl url = request.url().newBuilder().addQueryParameter(key, value).build();
        request = request.newBuilder().url(url).build();
        return chain.proceed(request);
    }
}
