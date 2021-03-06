package com.guess.image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.cdrongyao.caisichuan.R;
import com.guess.activity.SetQuestionActivity;
import com.guess.image.utils.ImageThumbnail;
import com.guess.image.view.ClipImageLayout;
import com.guess.user.ActivityMyInfo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class ClipImageActivity extends Activity implements OnClickListener {
	private String imagePath;
	private Button mCancel, mConfirm;
	private ClipImageLayout mClipImage;
	Bitmap bitmap, btm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clip_layout);
		imagePath = getIntent().getStringExtra("imagePath");

		initViews();
		if (!"".equals(imagePath)) {
			bitmap = BitmapFactory.decodeFile(imagePath);
			int scale = ImageThumbnail.reckonThumbnail(bitmap.getWidth(), bitmap.getHeight(), 700, 1200);
			btm = ImageThumbnail.PicZoom(bitmap, bitmap.getWidth() / scale, bitmap.getHeight() / scale);
			mClipImage.mClipView.setImageBitmap(btm);
		} else {
			Toast.makeText(ClipImageActivity.this, "图片选择失败！", Toast.LENGTH_SHORT).show();
		}
	}

	void initViews() {
		mClipImage = (ClipImageLayout) findViewById(R.id.photo_clip_layout);
		mCancel = (Button) findViewById(R.id.photo_clip_cancel);
		mConfirm = (Button) findViewById(R.id.photo_clip_confirm);

		mCancel.setOnClickListener(this);
		mConfirm.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.photo_clip_cancel:
			finish();
			break;
		case R.id.photo_clip_confirm:
			Bitmap bitmap = mClipImage.clip();
			Intent intent = new Intent();
			intent.putExtra("video", "");
			intent.putExtra("image", saveBitmap(bitmap));
			setResult(RESULT_OK, intent);
			finish();
			break;
		}
	}

	public String saveBitmap(Bitmap bitmap) {
		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CaiSiChuan";
		File file = new File(filePath);
		if (!file.exists())
			file.mkdir();

		String path = filePath + "/" + System.currentTimeMillis() + ".png";
		file = new File(path);
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 50, fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}

	@Override
	protected void onDestroy() {
		mClipImage.mClipView.setImageBitmap(null);
		bitmap.recycle();
		btm.recycle();
		super.onDestroy();
	}
}
