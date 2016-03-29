package com.guess.user;

import java.io.File;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.android.volley.VolleyError;
import com.guess.image.ClipImageActivity;
import com.guess.image.ListImgActivity;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.ShareData;
import com.guess.myutils.LoginUtil.LoginListener;
import com.guess.utils.PublicTools;
import com.guess.utils.PublicTools.UploadFinishListener;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 用户信息类
 * 
 * @author YXC
 *
 */
@SuppressLint("HandlerLeak")
public class ActivityMyInfo extends Activity implements OnClickListener {
	private TextView tvBack, tvTitle;
	private TextView tvCoin, tvIntegration;
	private TextView tvPhone;
	private RelativeLayout rltName, rltAddress, rltEdit, rltBind;
	private TextView tvName, tvAddress;
	private ImageView imgIcon;

	private ShareData share;
	private ApplicationUtil applicationUtil;

	private static final int FROM_ALBUM = 0;
	private static final int FROM_CAMERA = 1;
	private static final int HEAD_CROP = 2;

	private String headName = "";

	// private String userInfoUrl = "";//更新用户信息 包括电话 昵称和地址
	private String userAvatarUrl = "http://api.caisichuan.com/user/updateAvatar";// 更新头像
	
	private AlertDialog dialog;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FROM_ALBUM:
				// 从相册选取
				
				Intent store = new Intent(ActivityMyInfo.this, ListImgActivity.class);
				startActivityForResult(store, HEAD_CROP);
				break;

			case FROM_CAMERA:
				// 拍照获取头像
				saveFullImage();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myinfo);

		initView();

		String head = ShareData.getInstance(getApplicationContext()).getHead();
		if (!head.equals("")) {
			Picasso.with(ActivityMyInfo.this).load(head).resize(120, 120).placeholder(R.drawable.default_picture2x)
					.into(imgIcon);
		}
	}

	@Override
	protected void onResume() {
		initData();
		super.onResume();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// TODO Auto-generated method stub
		share = ShareData.getInstance(getApplicationContext());

		String phone = share.getPhone();
		// headName = path + share.getId() + "head2.jpg";
		tvCoin.setText(share.getCoin());
		tvIntegration.setText(share.getIntegration());
		tvName.setText(share.getNickName());
		tvAddress.setText(share.getAddress());
		// tvPhone.setText("".equals(phone) ?
		// getString(R.string.myinfo_phone_not_bind) : phone);

		if (share.getType() == 2) {
			// 电话账号
			tvPhone.setText(phone);
			rltEdit.setVisibility(View.VISIBLE);
			rltBind.setVisibility(View.GONE);
		} else {
			tvPhone.setText(R.string.myinfo_phone_not_bind);
			rltEdit.setVisibility(View.GONE);
			rltBind.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 初始化
	 */
	private void initView() {
		applicationUtil = new ApplicationUtil(getApplicationContext());

		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);

		tvBack.setText("我的");
		tvTitle.setText("个人资料");

		tvCoin = (TextView) findViewById(R.id.info_user_coin);
		tvIntegration = (TextView) findViewById(R.id.info_user_integration);
		tvPhone = (TextView) findViewById(R.id.info_user_id);
		tvName = (TextView) findViewById(R.id.info_tv_user_name);
		tvAddress = (TextView) findViewById(R.id.info_tv_user_address);

		imgIcon = (ImageView) findViewById(R.id.info_user_icon);

		rltName = (RelativeLayout) findViewById(R.id.info_rlt_name);
		rltAddress = (RelativeLayout) findViewById(R.id.info_rlt_address);
		rltEdit = (RelativeLayout) findViewById(R.id.info_rlt_edit_password);
		rltBind = (RelativeLayout) findViewById(R.id.info_rlt_bind_phone);

		// 为控件注册监听事件
		tvBack.setOnClickListener(this);
		rltAddress.setOnClickListener(this);
		rltEdit.setOnClickListener(this);
		rltBind.setOnClickListener(this);
		rltName.setOnClickListener(this);
		imgIcon.setOnClickListener(this);
		if (tvPhone.getText().length() != 11) {
			tvPhone.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.navigation_tv_back:
			// 返回
			finish();
			break;

		case R.id.info_rlt_address:
			// 修改收货地址
			intent = new Intent(ActivityMyInfo.this, EditAddress.class);
			startActivity(intent);
			break;

		case R.id.info_rlt_name:
			// 修改用户昵称
			intent = new Intent(ActivityMyInfo.this, EditNickName.class);
			startActivity(intent);
			break;

		case R.id.info_rlt_edit_password:
			// 修改密码
			intent = new Intent(ActivityMyInfo.this, EditPassword.class);
			startActivity(intent);
			break;
		case R.id.info_user_id:
		case R.id.info_rlt_bind_phone:
			// 非手机号码登录的用户绑定手机号码
			if (share.getType() == 0) {
				if (tvPhone.getText().length() != 11) {
					intent = new Intent(ActivityMyInfo.this, ActivityPassword.class);
					intent.putExtra("title", getString(R.string.title_phone_bind));
					intent.putExtra("operation", 4);
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(), getString(R.string.account_has_bind), Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(getApplicationContext(), getString(R.string.guest_can_bind), Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.info_user_icon:
			// 点击头像图标修改头像
			dialog = new AlertDialog.Builder(ActivityMyInfo.this).create();
			dialog.show();
			Window window = dialog.getWindow();
			window.setGravity(Gravity.CENTER_HORIZONTAL);
			window.setContentView(R.layout.myinfo_pick);

			Button btnStore = (Button) window.findViewById(R.id.info_pick_store);
			Button btnCamera = (Button) window.findViewById(R.id.info_pick_camera);
			TextView tvCancel = (TextView) window.findViewById(R.id.info_pick_cancel);
			btnCamera.setOnClickListener(this);
			btnStore.setOnClickListener(this);
			tvCancel.setOnClickListener(this);
			
			break;
		case R.id.info_pick_camera:
			Message msg = handler.obtainMessage();
			msg.what = FROM_CAMERA;
			handler.sendMessage(msg);
			dialog.dismiss();
			dialog.dismiss();
			break;
		case R.id.info_pick_store:
			// 相册选取
			Message msg1 = handler.obtainMessage();
			msg1.what = FROM_ALBUM;
			handler.sendMessage(msg1);
			dialog.dismiss();
			dialog.dismiss();
			break;
		case R.id.info_pick_cancel:
			dialog.dismiss();
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case FROM_CAMERA:
			if(resultCode ==RESULT_OK){
				Intent camera = new Intent(ActivityMyInfo.this, ClipImageActivity.class);
				camera.putExtra("imagePath", headName);
				startActivityForResult(camera, HEAD_CROP);
			}
			break;
		case HEAD_CROP:
			if (resultCode == RESULT_OK) {
				headName = data.getStringExtra("image");
				if(!"".equals(headName) && headName != null){
					imgIcon.setImageBitmap(BitmapFactory.decodeFile(headName));
					PublicTools.upload(getApplicationContext(), Integer.parseInt(share.getId()), headName, false,
							new UploadFinishListener() {
	
								@Override
								public void onAfterUpload() {
									// TODO Auto-generated method stub
									/**
									 * 上传头像到服务器
									 */
									HashMap<String, String> map = new HashMap<>();
									map.put("avatar",
											PublicTools.getImageUrl(Integer.parseInt(share.getId()), false));
									updateInfo(userAvatarUrl, map);
								}
							});
				}
			}
			break;
		default:
			break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	};

	/**
	 * 剪裁图片
	 * 
	 * @param uri
	 */
	public void cropPhoto(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 图片比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 输出图片大小
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, HEAD_CROP);
	}

	/**
	 * 更新用户信息
	 * 
	 * @param url
	 * @param map
	 */
	private void updateInfo(final String url, final HashMap<String, String> map) {
		MyJsonRequest jsonRequest = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response.getInt("ret") == 0) {
						share.setHead(map.get("avatar"));
						/*Picasso.with(ActivityMyInfo.this).load(map.get("avatar")).resize(120, 120)
								.placeholder(R.drawable.default_picture2x).into(imgIcon);*/
						Toast.makeText(getApplicationContext(), "头像上传成功", Toast.LENGTH_SHORT).show();
					} else {
						if (response.getInt("ret") == Constant.NOT_LOGIN) {
							LoginUtil login = new LoginUtil(ActivityMyInfo.this);
							login.login(new LoginListener() {

								@Override
								public void onLoginAfter() {
									updateInfo(url, map);
								}
							});
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

			}
		}, map);
		applicationUtil.getRequestQueue().add(jsonRequest);
	}
	
	private void saveFullImage() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 文件夹aaaa
		String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
		File path1 = new File(path);
		if (!path1.exists()) {
			path1.mkdirs();
		}
		File file = new File(path1, System.currentTimeMillis() + ".jpg");
		headName = file.getAbsolutePath();
		Uri mOutPutFileUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);
		startActivityForResult(intent, FROM_CAMERA);

	}

}
