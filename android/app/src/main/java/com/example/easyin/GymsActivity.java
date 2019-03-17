package com.example.easyin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.*;


public class GymsActivity extends AppCompatActivity {
    public static ArrayList<String> mGymNames = new ArrayList<>();
    public static ArrayList<String> mGymAddresses = new ArrayList<>();

    public static RecyclerViewAdapter adapter;

    private TextView locText;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyms);

        initLocation();
        HttpURLConnection.setFollowRedirects(true);
        if(mLocation != null)
        {
            //make get request
            String urlString = "https://api.yelp.com/v3/businesses/search?latitude=" + mLocation.getLatitude()
                    + "&longitude=" + mLocation.getLongitude()
                    + "&radius=8045&limit=10&categories=gyms";
            HttpGetRequest requester = new HttpGetRequest();
            requester.execute(urlString);
        }

        //create recyclerview
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter(this, mGymNames, mGymAddresses);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //location initialization
    private void initLocation()
    {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                mLocation = location;
            }

            @Override
            public void onProviderDisabled(String provider)
            {
                Intent sendToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(sendToSettings);
            }

            //methods that must be overridden for abstract class
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
        };

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]
                                {
                                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.INTERNET
                                },
                        10);
            }
            else
            {
                configureLocation();
            }
        }
        else
        {
            configureLocation();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case 10:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    configureLocation();
                }
                return;
        }
    }

    @SuppressLint("MissingPermission")
    private void configureLocation() {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLoc = null;
        String bestProvider = null;

        for(String provider : providers)
        {
            Location loc = mLocationManager.getLastKnownLocation(provider);
            if(loc == null)
            {
                continue;
            }

            if(bestLoc == null || loc.getAccuracy() < bestLoc.getAccuracy())
            {
                bestLoc = loc;
                bestProvider = provider;
            }
        }

        mLocation = bestLoc;
        if (bestProvider != null)
        {
            mLocationManager.requestLocationUpdates(bestProvider, 10000, 50, mLocationListener);
        }
    }
}




class HttpGetRequest extends AsyncTask<String, Void, String>
{
    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;

    @Override
    protected String doInBackground(String... strings)
    {
        try
        {
            URL url = new URL(strings[0]);

            //Create a connection
            HttpURLConnection connection =(HttpURLConnection)
            url.openConnection();

            //Set methods and timeouts
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            //add header
            connection.addRequestProperty("Authorization",
                    "Bearer Xfuotb5u_Nk40veUDNZ2v98_c4Q8lzYrfjFISortfazGyBOG-YcY84-YpcZfRvta4cWNx3tX_QqWQdxh9qKeIesqmezaDsIhVXU3JHRoF8NgJqQvWdtb3JpjxNqNXHYx");

            int code = connection.getResponseCode();

            //Connect to our url
            if(connection.getResponseCode() != 200)
            {
                throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
            }

            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            //Check if the line we are reading is not null
            String inputLine;
            while((inputLine = reader.readLine()) != null)
            {
                stringBuilder.append(inputLine);
            }

            //Close our InputStream and Buffered reader
            reader.close();

            //update result
            return stringBuilder.toString();
        }
        catch(IOException e)
        {
            Log.d("log", "bad");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        JSONObject jObj;
        try
        {
            jObj = new JSONObject(result);
            JSONArray gyms = jObj.getJSONArray("businesses");
            for(int i = 0; i < gyms.length(); i++)
            {
                JSONObject currentGym = (JSONObject)gyms.get(i);

                //add gym names to list
                GymsActivity.mGymNames.add((String)currentGym.get("name"));

                //add gym addresses
                JSONObject gymLocation = (JSONObject)currentGym.get("location");
                String address = (String)gymLocation.get("address1") + ", "
                        + (String)gymLocation.get("city") + " "
                        + (String)gymLocation.get("state") + ", "
                        + (String)gymLocation.get("zip_code");
                GymsActivity.mGymAddresses.add(address);

                GymsActivity.adapter.notifyDataSetChanged();
            }
        }
        catch(JSONException e)
        {
            Log.d("log", "json's shit hit the fan");
            throw new RuntimeException();
        }

    }
}
