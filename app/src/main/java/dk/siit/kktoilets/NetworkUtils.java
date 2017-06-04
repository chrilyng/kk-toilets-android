package dk.siit.kktoilets;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
    static final String URL_OPTION = "?";
    static final String URL_AND = "&";
    static final String PAGE_OPTION = "page=";
    static final String TYPE_OPTION = "type=";

    public static boolean checkConnectivity(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
