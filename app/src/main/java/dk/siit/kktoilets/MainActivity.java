package dk.siit.kktoilets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import dk.siit.kktoilets.model.Toilet;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final boolean DEBUG = false;
    public static final String BROADCAST_ANSWER_ACTION = "dk.siit.kktoilets.DOWNLOAD_RECEIVE";
    public static final String BROADCAST_ANSWER_EXTRA = "dk.siit.kktoilets.DOWNLOAD_EXTRA";

    private DownloadReceiver mDownloadReceiver;
    private RecyclerView mRecyclerView;
    private ToiletsAdapter mToiletsAdapter;
    private LinearLayoutManager mLayoutManager;
    private int mRequestPage = 0;
    private boolean mLoading = false;
    private int mVisibleItems = 0;
    private int mTotalItems = 0;
    private boolean mBottomHit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                if(!mBottomHit && !mLoading && mTotalItems - mVisibleItems < firstVisible + 5 && mTotalItems <= 80) {
                    mLoading = true;
                    Intent serviceIntent = new Intent(MainActivity.this, DownloadService.class);
                    serviceIntent.putExtra(DownloadService.DOWNLOAD_EXTRA_PAGE, ++mRequestPage);
                    startService(serviceIntent);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mDownloadReceiver = new DownloadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ANSWER_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mDownloadReceiver, intentFilter);

        mLoading = true;
        Intent serviceIntent = new Intent(this, DownloadService.class);
        serviceIntent.putExtra(DownloadService.DOWNLOAD_EXTRA_PAGE, 0);
        startService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDownloadReceiver);
        super.onDestroy();
    }

    private class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra(BROADCAST_ANSWER_EXTRA);
            Gson gson = new Gson();
            Toilet[] toilets = gson.fromJson(response, Toilet[].class);
            if(DEBUG) {
                for (Toilet toilet: toilets) {
                    Log.i(TAG, toilet.getName());
                    Log.i(TAG, toilet.getProperties().getToilet_type());
                }
            }
            mLoading = false;
            if(toilets.length<10) {
                // bottom is hit
                mBottomHit = true;
            }
            mToiletsAdapter.addToilets(Arrays.asList(toilets));
            mToiletsAdapter.notifyDataSetChanged();
            if(DEBUG) Log.i(TAG, "Adapter item count: "+mToiletsAdapter.getItemCount());
        }
    }

}
