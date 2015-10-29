package com.example.testapp;

import android.media.session.MediaController;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.VideoView;

public class VedioActivity extends ActionBarActivity {
    VideoView vid;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vedio);
        vid= (VideoView)findViewById(R.id.video);
        String uriPath= "android.resource://"+getPackageName()+"/"+ R.raw.fancy;
        vid.setVideoURI(Uri.parse(uriPath));
        vid.start();
  //  MediaController mediaController= new
	}



}
