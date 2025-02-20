package vn.tapbi.zazip.di;


import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.interfaces.onedriveApi.OnedriveApi;

@Module
@InstallIn(SingletonComponent.class)
public abstract class NetworkModule {

    @Provides
    @Singleton
    public static Retrofit providesRetrofit(
            OkHttpClient okHttpClient
    ) {
        return new Retrofit.Builder().baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    public static OnedriveApi onedriveApi(
            Retrofit retrofit
    ) {
        return retrofit.create(OnedriveApi.class);
    }

    @Provides
    @Singleton
    public static OkHttpClient providesOkHttpClientAppVersion() {
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .connectTimeout(Constant.CONNECT_S, TimeUnit.SECONDS)
                .writeTimeout(Constant.WRITE_S, TimeUnit.SECONDS)
                .readTimeout(Constant.READ_S, TimeUnit.SECONDS);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        client.addNetworkInterceptor(interceptor);
        return client.build();
    }
}