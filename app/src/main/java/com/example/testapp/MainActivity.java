package com.example.testapp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {
// this is a test for git 
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    private Uri fileUri; 
    private ImageView imgPreview;
	Button b1,btnCapturePicture;
    MyReceiver receiver= new MyReceiver();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		b1= (Button) findViewById(R.id.button1);
		imgPreview = (ImageView) findViewById(R.id.imageView1);
		btnCapturePicture = (Button) findViewById(R.id.button2);
		b1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),VedioActivity.class);
				startActivity(i);
			}
		});
		btnCapturePicture.setOnClickListener(new View.OnClickListener() {
			 
            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
            }
        });
		 if (!isDeviceSupportCamera()) {
	            Toast.makeText(getApplicationContext(),
	                    "Sorry! Your device doesn't support camera",
	                    Toast.LENGTH_LONG).show();
	            // will close the app if the device does't have camera
	            finish();
	        }

        IntentFilter intentFilter=new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        this.registerReceiver(receiver, intentFilter);


    }
    public void loc (View v){
		Intent j = new Intent(getApplicationContext(),MapActivity.class);
		startActivity(j);
	}
    public void contacts (View v){
        Intent j = new Intent(getApplicationContext(),ContactsList.class);
        startActivity(j);
    }
    public void address (View v){
        Intent j = new Intent(getApplicationContext(),MapActivity2.class);
        startActivity(j);
    }
    public void jsonBal(View v) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDialog()
                .build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
                .penaltyLog()
                .build());
        JSONObject json = null;
        String str = "";
        HttpResponse response;
        DefaultHttpClient myClient = new DefaultHttpClient();
        HttpPost myConnection = new HttpPost("http://demos.tricksofit.com/files/json.php");
        try {
            response = myClient.execute(myConnection);
            str = EntityUtils.toString(response.getEntity(), "UTF-8");
            // Toast.makeText(getApplicationContext(),str,Toast.LENGTH_LONG).show();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jArray = null;
        try {
            jArray = new JSONArray(str);
            json = jArray.getJSONObject(0);
            Toast.makeText(getApplicationContext(), json.getString("name"), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
	    /**
	     * Checking device has camera hardware or not
	     * */
	    private boolean isDeviceSupportCamera() {
	        if (getApplicationContext().getPackageManager().hasSystemFeature(
	                PackageManager.FEATURE_CAMERA)) {
	            // this device has a camera
	            return true;
	        } else {
	            // no camera on this device
	            return false;
	        }
	   }
	private void captureImage() {
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	 
	   fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
	 
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
	 
	    // start the image capture Intent
	    startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
	}
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
     
        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }
	/**
	 * Receiving activity result method will be called after closing the camera
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // if the result is capturing Image
	    if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            // successfully captured the image
	            // display it in image view
	            previewCapturedImage();
	        } else if (resultCode == RESULT_CANCELED) {
	            // user cancelled Image capture
	            Toast.makeText(getApplicationContext(),
	                    "User cancelled image capture", Toast.LENGTH_SHORT)
	                    .show();
	        } else {
	            // failed to capture image
	            Toast.makeText(getApplicationContext(),
	                    "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
	                    .show();
	        }
	    }
	}
	/*
     * Display image from a path to ImageView
     */
    private void previewCapturedImage() {
        try {
            // hide video preview
            //videoPreview.setVisibility(View.GONE);
 
            imgPreview.setVisibility(View.VISIBLE);
 
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();
 
            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;
 
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);
 
            imgPreview.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    
     
    /*
     * Here we restore the fileUri again
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
     
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
        previewCapturedImage();
    }
    // helper
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {
     
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
     
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
     
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
     
        return mediaFile;
    }
    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(receiver);
    }
}
