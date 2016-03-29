package com.guess.image;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.cdrongyao.caisichuan.R;
import com.guess.image.adapter.ListDirAdapter;
import com.guess.image.bean.FolderBeanParcel;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListImgActivity extends Activity {
	private TextView tvBack, tvTitle;
	private ListView mListView;
	private File mCurrentDir;
	private int mMaxCount;
	private ListDirAdapter mAdapter;
	private ProgressDialog mProgressDialog;
	private static final int SET_TAG = 0X110;
	private ArrayList<FolderBeanParcel> mFolderBeans = new ArrayList<FolderBeanParcel>();
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == SET_TAG) {

				mProgressDialog.dismiss();
				mAdapter.setData(mFolderBeans);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_window);
		
		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);
		tvBack.setText(R.string.back);
		tvTitle.setText("选择图片");
		tvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		initDatas();
		initViews();
		initEvents();
	}

	private void initEvents() {
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mCurrentDir = new File(mFolderBeans.get(position).getDir());
				Intent intent = new Intent(ListImgActivity.this, ShowImgActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("path", mCurrentDir.getAbsolutePath());
				intent.putExtras(bundle);
				startActivityForResult(intent, 10002);
			}
		});
	}

	private void initViews() {
		mListView = (ListView) findViewById(R.id.popup_window_listview);
		mAdapter = new ListDirAdapter(getApplicationContext());
		mListView.setAdapter(mAdapter);
	}

	private void initDatas() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "存储卡不可用", Toast.LENGTH_SHORT).show();
			return;
		}
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
		new Thread() {
			public void run() {
				Uri mimgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver cr = ListImgActivity.this.getContentResolver();
				Cursor cursor = cr.query(mimgUri, null,
						MediaStore.Images.Media.MIME_TYPE + " = ? or " + MediaStore.Images.Media.MIME_TYPE + " = ? ",
						new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);

				Set<String> mDirPaths = new HashSet<String>();

				while (cursor.moveToNext()) {
					String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();

					FolderBeanParcel folderBean = null;
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						folderBean = new FolderBeanParcel();
						folderBean.setDir(dirPath);
						folderBean.setFirstImgPath(path);
					}
					if (parentFile.list() == null)
						continue;
					int picSize = parentFile.list(new FilenameFilter() {

						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png")) {
								return true;
							}
							return false;
						};
					}).length;
					folderBean.setCount(picSize);
					mFolderBeans.add(folderBean);
					if (picSize > mMaxCount) {
						mMaxCount = picSize;
						mCurrentDir = parentFile;
					}
				}
				cursor.close();
				mHandler.sendEmptyMessage(SET_TAG);

			};
		}.start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if((requestCode == 10002) && (resultCode == RESULT_OK)){
			setResult(RESULT_OK, data);
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
