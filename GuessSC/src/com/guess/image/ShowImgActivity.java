package com.guess.image;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import com.cdrongyao.caisichuan.R;
import com.guess.image.ClipImageActivity;
import com.guess.image.adapter.ImageAdapter;
import com.guess.image.bean.ImageMsg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

public class ShowImgActivity extends Activity implements OnClickListener, OnItemClickListener {
	private GridView mGridView;
	private ImageAdapter mAdapter;
	private File mCurrentDir;
	private String mDir;
	private Button btnFinish;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_show);
		mGridView = (GridView) findViewById(R.id.show_image_gridview);
		btnFinish = (Button) findViewById(R.id.show_image_back);
		btnFinish.setOnClickListener(this);
		mGridView.setOnItemClickListener(this);
		mDir = this.getIntent().getExtras().getString("path");
		initDatas();
	}

	private void initDatas() {
		mCurrentDir = new File(mDir);
		ImageMsg.mAllPath = Arrays.asList(mCurrentDir.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png"))
					return true;

				return false;
			}

		}));
		mAdapter = new ImageAdapter(ShowImgActivity.this, mDir);
		mAdapter.setData(ImageMsg.mAllPath);
		mGridView.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.show_image_back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Toast.makeText(ShowImgActivity.this, "" + mCurrentDir + "/" + ImageMsg.mAllPath.get(position),
				Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(ShowImgActivity.this, ClipImageActivity.class);
		intent.putExtra("imagePath", "" + mCurrentDir + "/" + ImageMsg.mAllPath.get(position));
		startActivityForResult(intent, 10003);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if((requestCode == 10003) && (resultCode == RESULT_OK)){
			setResult(RESULT_OK, data);
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
