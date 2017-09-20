package com.exercise.eugene.pixabay.client;

import com.exercise.eugene.pixabay.BuildConfig;
import com.exercise.eugene.pixabay.model.Pixabay;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PixabayService {

    enum ORDER {
        popular,
        latest
    }

    @GET(BuildConfig.KEY)
    Observable<Pixabay> getPhotos(
            @Query("editors_choice") boolean showEditorsChoice,
            @Query("order") ORDER popularOrLatest,
            @Query("category") String category,
            @Query("page") int page);


    @GET(BuildConfig.KEY)
    Observable<Pixabay> getSearchPhotos(
            @Query("q") String search,
            @Query("page") int page);


    class Factory {
        public static PixabayService create() {
            Retrofit.Builder builder = new Retrofit.Builder();
            builder.baseUrl(BuildConfig.END_POINT);
            builder.addConverterFactory(GsonConverterFactory.create());
            builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
            builder.client(http3Client());
            builder.build();
            return builder.build().create(PixabayService.class);
        }

        private static OkHttpClient http3Client() {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .cipherSuites(
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                    .build();
            builder.connectionSpecs(Collections.singletonList(spec));
            builder.connectTimeout(BuildConfig.TIMEOUT, TimeUnit.SECONDS);
            builder.writeTimeout(BuildConfig.TIMEOUT, TimeUnit.SECONDS);
            builder.readTimeout(BuildConfig.TIMEOUT, TimeUnit.SECONDS);
            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    HttpUrl originalHttpUrl = original.url();
                    HttpUrl url = originalHttpUrl
                            .newBuilder()
//                            .addQueryParameter("key", BuildConfig.KEY) // Causing issues due to the Question mark before the KEY
                            .addQueryParameter("image_type", "photo")
                            .addQueryParameter("pretty", "false")
                            .addQueryParameter("per_page", "20")
                            .build();
                    Request.Builder requestBuilder = original
                            .newBuilder()
                            .url(url);
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
            builder.addInterceptor(loggingInterceptor());
            return builder.build();
        }

        /**
         * Log the body of the responses
         *
         * @return Logging Interceptor
         */
        private static HttpLoggingInterceptor loggingInterceptor() {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            return logging;
        }
    }

}
