package com.jaykapadia.weather;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements LocationListener {
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
    final String APP_ID = "26a5f716493a176261a9eea2c216d1fd";
    final int REQUEST_CODE = 123;
    String cityname;
    ProgressBar p1;
    TextView t1;
    EditText e1;
    RecyclerView r1;
    LinearLayoutManager manager;
    LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        e1 = findViewById(R.id.cityname);
        ImageButton i1 = findViewById(R.id.getData);

        r1 = findViewById(R.id.rec);
        manager = new LinearLayoutManager(this);
        t1 = findViewById(R.id.tf);
        p1 = findViewById(R.id.pbar);


        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }


        e1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    cityname = e1.getText().toString();
                    RequestParams params = new RequestParams();
                    params.put("q", cityname);
                    params.put("cnt", 14);
                    params.put("units", "metric");
                    params.put("appid", APP_ID);
                    getwedata(params);
                }
                return false;
            }
        });


        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (e1.getText().toString().length() == 0) {
                    getweloc();
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_HIDDEN);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    cityname = e1.getText().toString();
                    RequestParams params = new RequestParams();
                    params.put("q", cityname);
                    params.put("cnt", 14);
                    params.put("units", "metric");
                    params.put("appid", APP_ID);
                    getwedata(params);
                }
            }
        });


    }

    private void getwedata(RequestParams params) {
        p1.setVisibility(View.VISIBLE);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    p1.setVisibility(View.GONE);
                    t1.setVisibility(View.VISIBLE);
                    adapter adapter = new adapter(getApplicationContext(), response.getJSONArray("list"));
                    r1.setLayoutManager(manager);
                    r1.setAdapter(adapter);
                    if (e1.getText().toString().length() == 0) {
                        e1.setText(response.getJSONObject("city").getString("name"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                p1.setVisibility(View.GONE);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Error");
                try {
                    builder.setMessage(errorResponse.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });
    }


    private void getweloc() {
        p1.setVisibility(View.VISIBLE);
        try {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getweloc();
            } else {
                Log.d("location", "permission denied");
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        RequestParams params = new RequestParams();
        params.put("lat", location.getLatitude());
        params.put("lon", location.getLongitude());
        params.put("cnt", 14);
        params.put("units", "metric");
        params.put("appid", APP_ID);
        getwedata(params);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationManager != null) mLocationManager.removeUpdates(this);
    }
}
