package com.smartserver.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.smartserver.core.SmartServer;
import com.smartserver.core.base.Config;
import com.smartserver.core.reader.AndroidAssetReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("---", "open http://" + Utils.getIpAddress(getApplicationContext()));
        Config config = new Config.Builder()
                .port(8888)
                .assetReader(new AndroidAssetReader(getAssets()))
                .build();
        SmartServer.start(config);
    }
}
