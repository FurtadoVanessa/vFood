package com.example.vanes.vfood;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.internal.Constants;

public class Main2Activity extends AppCompatActivity {

    public static final int MY_PERMISSIONS = 0;

    TextView message;
    Button openApp;
    private GoogleApiClient client;
    private Weather weather;
    private String ifoodPackage = "br.com.brainweb.ifood";
    private String marketLink = "market://details?id=";
    private String permissionDenied = "Sua localização é essencial para indicarmos o melhor produto a você";
    private String permissionTitle = "Localização Necessária";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        message = (TextView) findViewById(R.id.message);
        openApp = (Button) findViewById(R.id.button);
        openApp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = getPackageManager().getLaunchIntentForPackage(ifoodPackage);
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(marketLink + ifoodPackage));
                    startActivity(intent);
                }
            }
        });


        connectGoogleAPI();

        getWeatherData();
        }

    private void connectGoogleAPI(){
        client = new GoogleApiClient.Builder(this.getApplicationContext())
                .addApi(Awareness.API)
                .build();
        client.connect();
    }

    private void getWeatherData(){
        if (ContextCompat.checkSelfPermission(
                Main2Activity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Main2Activity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS);
        }else{
            Awareness.SnapshotApi.getWeather(client)
                    .setResultCallback(new ResultCallback<WeatherResult>() {
                        @Override
                        public void onResult(@NonNull WeatherResult weatherResult) {
                            if (!weatherResult.getStatus().isSuccess()) {
                                Log.e("######", "Could not get weather.");
                                return;
                            }
                            weather = weatherResult.getWeather();
                            Log.i("#####", "Weather: " + weather);
                            if (weather.getTemperature(Weather.CELSIUS) < 25)
                                message.setText("Que tal uma pizza?");
                            else
                                message.setText("Que tal um sorvete?");
                        }
                    });
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getWeatherData();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
                    builder.setMessage(permissionDenied)
                            .setTitle(permissionTitle);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    dialog.hide();
                    ActivityCompat.requestPermissions(Main2Activity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS);
                    builder = new AlertDialog.Builder(Main2Activity.this);
                    builder.setMessage("Nosso aplicativo não funciona sem sua localização")
                            .setTitle(permissionTitle);

                    dialog = builder.create();
                    dialog.show();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
        }
    }
}
