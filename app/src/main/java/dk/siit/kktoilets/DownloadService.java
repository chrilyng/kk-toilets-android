package dk.siit.kktoilets;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadService extends IntentService {
    private static final String TAG = DownloadService.class.getName();
    private static final String DOWNLOAD_URL = "https://kk-toilets.herokuapp.com/toilet";
    public static final String DOWNLOAD_EXTRA_PAGE = "dk.siit.kktoilets.DOWNLOAD_EXTRA_PAGE";

    private OkHttpClient client;

    public DownloadService() {
        super("kk-toilets-download-service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        int cacheSize = 10 * 1024 * 1024; // 10MB
        Cache cache = new Cache(getCacheDir(), cacheSize);
        client = new OkHttpClient.Builder().cache(cache).connectTimeout(10000, TimeUnit.SECONDS)
                .build();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            int page = intent.getIntExtra(DOWNLOAD_EXTRA_PAGE, 0);
            Log.i(TAG, "Request page "+page);
            String response = doRequest(DOWNLOAD_URL+"?page="+page);
            Intent broadcastIntent = new Intent(MainActivity.BROADCAST_ANSWER_ACTION);
            broadcastIntent.putExtra(MainActivity.BROADCAST_ANSWER_EXTRA, response);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String doRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
