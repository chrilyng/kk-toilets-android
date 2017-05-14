package dk.siit.kktoilets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Arrays;

import dk.siit.kktoilets.model.Toilet;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final boolean DEBUG = false;
    public static final String BROADCAST_ANSWER_ACTION = "dk.siit.kktoilets.DOWNLOAD_RECEIVE";
    public static final String BROADCAST_ANSWER_EXTRA = "dk.siit.kktoilets.DOWNLOAD_EXTRA";
    private static final int FULL_PAGE_COUNT = 10;
    private static final int HALF_PAGE_COUNT = FULL_PAGE_COUNT/2;

    private DownloadReceiver mDownloadReceiver;
    private RecyclerView mRecyclerView;
    private ToiletsAdapter mToiletsAdapter;
    private LinearLayoutManager mLayoutManager;
    private AdView mAdView;
    private int mRequestPage = 0;
    private boolean mLoading = false;
    private int mVisibleItems = 0;
    private int mTotalItems = 0;
    private boolean mBottomHit = false;
    private boolean mNoInternet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, getString(R.string.ad_id));
        mAdView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mToiletsAdapter = new ToiletsAdapter(new ArrayList<Toilet>(0));
        mRecyclerView.setAdapter(mToiletsAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mTotalItems = mLayoutManager.getItemCount();
                int firstVisible = mLayoutManager.findFirstVisibleItemPosition();
                mVisibleItems = mRecyclerView.getChildCount();

                if(DEBUG) Log.i(TAG, "Visible items: "+mVisibleItems+" total items: "+mTotalItems + " bottom hit: "+mBottomHit);

                if(checkConnectivity()) {
                    if(!mBottomHit && !mLoading && mTotalItems - mVisibleItems < firstVisible + HALF_PAGE_COUNT) {
                        mLoading = true;
                        Intent serviceIntent = new Intent(MainActivity.this, DownloadService.class);
                        serviceIntent.putExtra(DownloadService.DOWNLOAD_EXTRA_PAGE, ++mRequestPage);
                        startService(serviceIntent);
                    }
                } else {
                    showNoInternetDialog();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mDownloadReceiver = new DownloadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ANSWER_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mDownloadReceiver, intentFilter);

        if(checkConnectivity()) {
            mLoading = true;
            Intent serviceIntent = new Intent(this, DownloadService.class);
            serviceIntent.putExtra(DownloadService.DOWNLOAD_EXTRA_PAGE, mRequestPage);
            startService(serviceIntent);
        } else {
            showNoInternetDialog();
            mToiletsAdapter.setLoadComplete(true);
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDownloadReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mNoInternet) {
            if(checkConnectivity()) {
                mNoInternet = false;
                mLoading = true;
                mToiletsAdapter.setLoadComplete(false);
                Intent serviceIntent = new Intent(this, DownloadService.class);
                serviceIntent.putExtra(DownloadService.DOWNLOAD_EXTRA_PAGE, mRequestPage);
                startService(serviceIntent);
            } else {
                showNoInternetDialog();
                mToiletsAdapter.setLoadComplete(true);
            }
        }
    }

    private boolean checkConnectivity() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void showNoInternetDialog() {
        if(!mNoInternet) {
            mNoInternet = true;
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
            dialogBuilder.setMessage(R.string.no_internet).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    mLoading = false;
                }
            }).create().show();
        }
    }

    private class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mLoading = false;
            String response = intent.getStringExtra(BROADCAST_ANSWER_EXTRA);
            Gson gson = new Gson();
            try {
                Toilet[] toilets = gson.fromJson(response, Toilet[].class);
                if (DEBUG) {
                    for (Toilet toilet : toilets) {
                        Log.i(TAG, toilet.getName());
                        Log.i(TAG, toilet.getProperties().getToilet_type());
                    }
                }
                if (toilets.length < FULL_PAGE_COUNT) {
                    mBottomHit = true;
                    mToiletsAdapter.setLoadComplete(true);
                }
                mToiletsAdapter.addToilets(Arrays.asList(toilets));
                mToiletsAdapter.notifyDataSetChanged();
                if (DEBUG) Log.i(TAG, "Adapter item count: " + mToiletsAdapter.getItemCount());
            } catch(JsonSyntaxException ex) {
                Log.e(TAG, "Error loading toilets", ex);
                mToiletsAdapter.setLoadComplete(true);
            }
        }
    }
}
