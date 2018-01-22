package dk.siit.kktoilets;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ToiletAppModule {
    ToiletApp mApplication;

    public ToiletAppModule(ToiletApp application) {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    ToiletApp providesApplication() {
        return mApplication;
    }
}
