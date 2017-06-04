package dk.siit.kktoilets;

import android.app.Application;

public class ToiletApp extends Application {
    static final String BASE_URL = "https://kk-toilets.herokuapp.com/";
    static final String DOWNLOAD_URL = "https://kk-toilets.herokuapp.com/toilet";
    static final String CITY_URL = "https://kk-toilets.herokuapp.com/bydele";
    static final String TYPE_URL = "https://kk-toilets.herokuapp.com/typer";
    static final String TIME_URL = "https://kk-toilets.herokuapp.com/tider";
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
