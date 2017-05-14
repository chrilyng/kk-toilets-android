package dk.siit.kktoilets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import dk.siit.kktoilets.model.Type;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = SearchActivity.class.getName();
    private static final boolean DEBUG = false;
    public static final String BROADCAST_TYPE_ANSWER_ACTION = "dk.siit.kktoilets.BROADCAST_TYPE_RECEIVE";
    public static final String BROADCAST_TYPE_ANSWER_EXTRA = "dk.siit.kktoilets.BROADCAST_TYPE_EXTRA";

    private Spinner mTypeSpinner;
    private TypeReceiver mTypeReceiver;
    private Button mSearchButton;
    private String mCurrentCity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mTypeSpinner = (Spinner) findViewById(R.id.city_spinner);
        mTypeReceiver = new TypeReceiver();
        mSearchButton = (Button) findViewById(R.id.search_button);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(SearchActivity.this, MainActivity.class);
                mainActivity.putExtra(MainActivity.INTENT_CITY_EXTRA, mCurrentCity);
                startActivity(mainActivity);
            }
        });

        IntentFilter cityIntentFilter = new IntentFilter();
        cityIntentFilter.addAction(BROADCAST_TYPE_ANSWER_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mTypeReceiver, cityIntentFilter);

        if(NetworkUtils.checkConnectivity(SearchActivity.this)) {
            Intent cityServiceIntent = new Intent(this, DownloadService.class);
            cityServiceIntent.setAction(DownloadService.TYPE_ACTION);
            startService(cityServiceIntent);
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTypeReceiver);
        super.onDestroy();
    }

    private class TypeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra(BROADCAST_TYPE_ANSWER_EXTRA);
            Gson gson = new Gson();
            try {
                Type[] types = gson.fromJson(response, Type[].class);
                final String[] typeStrings = new String[types.length];
                for (int i=0; i<types.length; i++) {
                    if (DEBUG)
                        Log.i(TAG, "Type id: " + types[i].get_id());
                    typeStrings[i]=types[i].get_id();
                }
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, typeStrings);
                mTypeSpinner.setAdapter(cityAdapter);
                mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mCurrentCity = typeStrings[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        mCurrentCity = "";
                    }
                });
            } catch (JsonSyntaxException ex) {
                Log.e(TAG, "Error loading cities", ex);
            }
        }
    }

}
