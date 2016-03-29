package com.guess.image.video;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.cdrongyao.caisichuan.R;
import com.guess.activity.SetQuestionActivity;
import com.guess.image.ClipImageActivity;
import com.yixia.camera.util.DeviceUtils;
import com.yixia.camera.util.StringUtils;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * 单独播放界面
 */
public class VideoPlayerActivity extends MediaBaseActivity implements SurfaceVideoView.OnPlayStateListener,
		OnErrorListener, OnPreparedListener, OnClickListener, OnCompletionListener, OnInfoListener {

	/** 播放控件 */
	private SurfaceVideoView mVideoView;
	/** 暂停按钮 */
	private View mPlayerStatus;
	private View mLoading;
	private Button mReTakeBtn, mConfirmBtn;
	/** 播放路径 */
	private String mImagePath, mVideoPath;

	/** 是否需要回复播放 */
	private boolean mNeedResume;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 防止锁屏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mVideoPath = getIntent().getStringExtra("path");
		if (StringUtils.isEmpty(mVideoPath)) {
			finish();
			return;
		}

		setContentView(R.layout.activity_video_player);
		mVideoView = (SurfaceVideoView) findViewById(R.id.videoview);
		mPlayerStatus = findViewById(R.id.play_status);
		mLoading = findViewById(R.id.loading);
		mReTakeBtn = (Button) findViewById(R.id.video_play_retake);
		mConfirmBtn = (Button) findViewById(R.id.video_play_confirm);

		mReTakeBtn.setOnClickListener(this);
		mConfirmBtn.setOnClickListener(this);
		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnPlayStateListener(this);
		mVideoView.setOnErrorListener(this);
		mVideoView.setOnClickListener(this);
		mVideoView.setOnInfoListener(this);
		mVideoView.setOnCompletionListener(this);

		mVideoView.getLayoutParams().height = DeviceUtils.getScreenWidth(this);

		findViewById(R.id.root).setOnClickListener(this);

		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(mVideoPath);
		Bitmap bitmap = mmr.getFrameAtTime();
		mImagePath = saveBitmap(bitmap);
		mVideoView.setVideoPath(mVideoPath);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mVideoView != null && mNeedResume) {
			mNeedResume = false;
			if (mVideoView.isRelease())
				mVideoView.reOpen();
			else
				mVideoView.start();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mVideoView != null) {
			if (mVideoView.isPlaying()) {
				mNeedResume = true;
				mVideoView.pause();
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (mVideoView != null) {
			mVideoView.release();
			mVideoView = null;
		}
		super.onDestroy();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mVideoView.setVolume(SurfaceVideoView.getSystemVolumn(this));
		mVideoView.start();
		mLoading.setVisibility(View.GONE);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getKeyCode()) {// 跟随系统音量走
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			mVideoView.dispatchKeyEvent(this, event);
			break;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onStateChanged(boolean isPlaying) {
		mPlayerStatus.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (!isFinishing()) {
			// 播放失败
		}
		finish();
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.root:
			finish();
			break;
		case R.id.video_play_retake:
			finish();
			break;
		case R.id.video_play_confirm:
			Intent intent = new Intent();
			intent.putExtra("video", mVideoPath);
			intent.putExtra("image", mImagePath);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.videoview:
			if (mVideoView.isPlaying())
				mVideoView.pause();
			else
				mVideoView.start();
			break;
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// if (!isFinishing()) {
		// mVideoView.reOpen();
		mVideoView.pause();
		mPlayerStatus.setVisibility(View.VISIBLE);
		// }
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		switch (what) {
		case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
			// 音频和视频数据不正确
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			if (!isFinishing())
				mVideoView.pause();
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			if (!isFinishing())
				mVideoView.start();
			break;
		case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
			if (DeviceUtils.hasJellyBean()) {
				mVideoView.setBackground(null);
			} else {
				mVideoView.setBackgroundDrawable(null);
			}
			break;
		}
		return false;
	}

	public String saveBitmap(Bitmap bitmap) {
		String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/CaiSiChuan";
		File file = new File(filePath);
		if (!file.exists())
			file.mkdir();

		String path = filePath + "/" + System.currentTimeMillis() + ".png";
		file = new File(path);
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}

}
