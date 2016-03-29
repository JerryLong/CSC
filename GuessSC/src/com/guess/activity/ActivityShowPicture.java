package com.guess.activity;

import com.cdrongyao.caisichuan.R;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ActivityShowPicture extends Activity{
	
	private RelativeLayout rlt;
	
	private ImageView imgPicture;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_picture);
		
		
		rlt = (RelativeLayout) findViewById(R.id.show_picture);
		imgPicture = (ImageView) findViewById(R.id.show_picture_picture);
		
		String url = getIntent().getStringExtra("imageUrl");
		Picasso.with(this).load(url).resize(300, 300).placeholder(R.drawable.default_picture2x).into(imgPicture);
		
		rlt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
}
