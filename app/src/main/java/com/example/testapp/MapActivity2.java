package com.example.testapp;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MapActivity2 extends FragmentActivity  {

    GoogleMap googleMap2;
    double longitute,latitude;
    EditText editText;
    Button button;
    String s;
    SupportMapFragment supportMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        setContentView(R.layout.activity_map_activity2);
        editText= (EditText) findViewById(R.id.editText);
        button= (Button) findViewById(R.id.button4);
        supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        googleMap2 = supportMapFragment.getMap();
        googleMap2.setMyLocationEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s= editText.getText().toString();
                Background bg= new Background();
                bg.execute(s);
            }
        });


    }
    public class Background extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... params) {
            getLatLong(getLocationInfo(params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            googleMap2 = supportMapFragment.getMap();
            googleMap2.setMyLocationEnabled(true);
            googleMap2.isBuildingsEnabled();
            googleMap2.isTrafficEnabled();
            googleMap2.isIndoorEnabled();
           // googleMap2.isMyLocationEnabled();
            LatLng latLng = new LatLng(latitude, longitute);
            googleMap2.addMarker(new MarkerOptions().position(latLng));
            googleMap2.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap2.animateCamera(CameraUpdateFactory.zoomTo(15));
          /*  Location location= googleMap2.getMyLocation();
            double latitude2=location.getLatitude();
            double longitute2=location.getLongitude();
            LatLng latLng2= new LatLng(latitude2,longitute2);
            googleMap2.addMarker(new MarkerOptions().position(latLng2).title("farhan"));
            googleMap2.moveCamera(CameraUpdateFactory.newLatLng(latLng2));
            */

        }
    }
    public JSONObject getLocationInfo(String address) {
        StringBuilder stringBuilder = new StringBuilder();
        try {

            address = address.replaceAll(" ","%20");

            HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();


            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }
    public boolean getLatLong(JSONObject jsonObject) {

        try {

            longitute = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            latitude = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

        } catch (JSONException e) {
            return false;

        }

        return true;
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }
}