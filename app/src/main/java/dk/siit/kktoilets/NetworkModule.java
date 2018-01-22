package dk.siit.kktoilets;

import android.app.Application;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

@Module
public class NetworkModule {
    String mBaseURL;

    public NetworkModule(String baseURL) {
        this.mBaseURL = baseURL;
    }

    @Provides
    @Singleton
    Cache providesOkHttpCache(ToiletApp application) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(application.getCacheDir(), cacheSize);
        return cache;
    }

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient(Cache cache) {
        OkHttpClient client = new OkHttpClient.Builder().cache(cache).connectTimeout(10000, TimeUnit.SECONDS)
                .build();
        return client;
    }
}
