package dk.siit.kktoilets;

import android.app.Application;

public class ToiletApp extends Application {
    private static final String BASE_URL = "https://kk-toilets.herokuapp.com/";
    private NetworkComponent mNetworkComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mNetworkComponent = DaggerNetworkComponent.builder()
                .networkModule(new NetworkModule(BASE_URL))
                .toiletAppModule(new ToiletAppModule(this)).build();
    }

    public NetworkComponent getNetworkComponent() {
        return mNetworkComponent;
    }
}
