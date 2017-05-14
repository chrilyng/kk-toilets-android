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
    private static final String CITY_URL = "https://kk-toilets.herokuapp.com/bydele";
    private static final String TYPE_URL = "https://kk-toilets.herokuapp.com/typer";
    private static final String TIME_URL = "https://kk-toilets.herokuapp.com/tider";
    public static final String DOWNLOAD_ACTION = "dk.siit.kktoilets.DOWNLOAD_ACTION";
    public static final String TYPE_ACTION = "dk.siit.kktoilets.TYPE_ACTION";
    public static final String DOWNLOAD_EXTRA_PAGE = "dk.siit.kktoilets.DOWNLOAD_EXTRA_PAGE";
    public static final String DOWNLOAD_EXTRA_TYPE = "dk.siit.kktoilets.DOWNLOAD_EXTRA_TYPE";
    private static final String URL_OPTION = "?";
    private static final String URL_AND = "&";
    private static final String PAGE_OPTION = "page=";
    private static final String TYPE_OPTION = "type=";

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
        if(intent.getAction().equals(DOWNLOAD_ACTION)) {
            try {
                int page = intent.getIntExtra(DOWNLOAD_EXTRA_PAGE, 0);
                String city = intent.getStringExtra(DOWNLOAD_EXTRA_TYPE);
                Log.i(TAG, "Request page " + page);
                StringBuffer urlStringBuffer = new StringBuffer(DOWNLOAD_URL);
                urlStringBuffer.append(URL_OPTION);
                urlStringBuffer.append(PAGE_OPTION);
                urlStringBuffer.append(page);
                if(city!=null) {
//                    Log.i(TAG, "Request city " + city);
                    urlStringBuffer.append(URL_AND);
                    urlStringBuffer.append(TYPE_OPTION);
                    urlStringBuffer.append(city);
                }
                String response = doRequest(urlStringBuffer.toString());
                Intent broadcastIntent = new Intent(MainActivity.BROADCAST_ANSWER_ACTION);
                broadcastIntent.putExtra(MainActivity.BROADCAST_ANSWER_EXTRA, response);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
            } catch (IOException e) {
                e.printStackTrace();
                Intent broadcastIntent = new Intent(MainActivity.BROADCAST_ANSWER_ACTION);
                broadcastIntent.putExtra(MainActivity.BROADCAST_ANSWER_EXTRA, ""); // TODO error message
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
            }
        } else if(intent.getAction().equals(TYPE_ACTION)) {
            try {
                String response = doRequest(TYPE_URL);
                Intent broadcastIntent = new Intent(SearchActivity.BROADCAST_TYPE_ANSWER_ACTION);
                broadcastIntent.putExtra(SearchActivity.BROADCAST_TYPE_ANSWER_EXTRA, response);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
            } catch (IOException e) {
                e.printStackTrace();
                Intent broadcastIntent = new Intent(SearchActivity.BROADCAST_TYPE_ANSWER_ACTION);
                broadcastIntent.putExtra(MainActivity.BROADCAST_ANSWER_EXTRA, ""); // TODO error message
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
            }
        }
    }


    private String doRequest(String url) throws IOException {
//        Log.d(TAG, "Do internet request: "+url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
