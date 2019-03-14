package com.hmproductions.aidlconfig;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import static com.hmproductions.aidlconfig.Miscellaneous.convertImplicitIntentToExplicitIntent;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Boolean> {

    private static final int PROXIMITY_LOADER_ID = 101;

    IProximityInterface proximityInterface;

    Button connectButton, proximityButton, disconnectButton;
    TextView durationTextView, distanceTextView;

    long startTime;
    boolean serviceConnected = false;

    private ServiceConnection proximityConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            proximityInterface = IProximityInterface.Stub.asInterface(iBinder);
            Toast.makeText(MainActivity.this, "Service connected", Toast.LENGTH_SHORT).show();
            serviceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast.makeText(MainActivity.this, "Service disconnected", Toast.LENGTH_SHORT).show();
            serviceConnected = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectButton = findViewById(R.id.connect_button);
        proximityButton = findViewById(R.id.proximity_button);
        disconnectButton = findViewById(R.id.disconnect_button);
        durationTextView = findViewById(R.id.duration_textView);
        distanceTextView = findViewById(R.id.distance_textView);

        SetClickListeners();
    }

    private void SetClickListeners() {

        connectButton.setOnClickListener(v -> {
            if (!serviceConnected) {
                Intent intent = new Intent("com.hmproductions.service.PROXIMITY");
                bindService(convertImplicitIntentToExplicitIntent(intent, this), proximityConnection, BIND_AUTO_CREATE);
            } else {
                Toast.makeText(this, "Service already connected", Toast.LENGTH_SHORT).show();
            }
        });

        proximityButton.setOnClickListener(v -> {
            if (serviceConnected)
                getSupportLoaderManager().restartLoader(PROXIMITY_LOADER_ID, null, this);
            else {
                Toast.makeText(this, "Please connect first", Toast.LENGTH_SHORT).show();
            }
        });

        disconnectButton.setOnClickListener(view -> {
            if (serviceConnected) {
                unbindService(proximityConnection);
                proximityInterface = null;
                serviceConnected = false;
                Toast.makeText(this, "Service disconnected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Service not connected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public Loader<Boolean> onCreateLoader(int id, @Nullable Bundle args) {
        startTime = System.currentTimeMillis();
        return new ProximityLoader(this, proximityInterface);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Boolean> loader, Boolean data) {

        String temp = String.valueOf(System.currentTimeMillis() - startTime) + " ms";
        durationTextView.setText(temp);

        temp = data ? "NEAR" : "FAR";
        distanceTextView.setText(temp);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Boolean> loader) {
        // Do nothing
    }
}
