package com.guess.user;



import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cdrongyao.caisichuan.R;
import com.guess.myutils.ApplicationUtil;
import com.guess.myutils.Constant;
import com.guess.myutils.LoginUtil;
import com.guess.myutils.MyJsonRequest;
import com.guess.myutils.MyNetManager;
import com.guess.myutils.ShareData;
import com.guess.myutils.LoginUtil.LoginListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class EditAddress extends Activity implements OnClickListener{
	private InputMethodManager imm;
	private ShareData share;
	
	private String address;
	
	private TextView tvBack, tvTitle;
	
	private EditText etAddress;
	
	private Button btnSave;
	
	private String userAddrUrl = "http://api.caisichuan.com/user/updateAddr";//更新地址

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_address);
	
		initView();
	}
	/**
	 * 初始化
	 */
	private void initView() {
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		share = ShareData.getInstance(getApplicationContext());
		
		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);
		
		etAddress = (EditText) findViewById(R.id.edit_et_address);

		btnSave = (Button) findViewById(R.id.edit_address_save);
		
		tvBack.setText(R.string.title_back);
		tvTitle.setText(R.string.title_user_address);
		
		address = share.getAddress();
		etAddress.setText(address);
		
		tvBack.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		if(imm.isActive()){
			imm.hideSoftInputFromWindow(etAddress.getWindowToken(), 0);
		}
		switch (v.getId()) {
		case R.id.navigation_tv_back:
			finish();
			break;
		case R.id.edit_address_save:
			//保存修改
			if(new MyNetManager(getApplicationContext()).netIsAvailable()){
				String strAddress= etAddress.getText().toString().trim();
				if(!strAddress.equals(address)){
					address = strAddress;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("addr", strAddress);
					updateInfo(userAddrUrl, map);
					btnSave.setClickable(false);
					
				}
			}else{
				Toast.makeText(getApplicationContext(), getString(R.string.network_not_available), Toast.LENGTH_SHORT).show();
			}
			break;
		}
		
	}
	
	/**
	 *更新用户信息
	 * @param url
	 * @param map
	 */
	private void updateInfo(final String url, final HashMap<String, String> map){
		MyJsonRequest jsonRequest = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if(response.getInt("ret") == 0){
						Toast.makeText(getApplicationContext(), getString(R.string.info_edit_success), Toast.LENGTH_SHORT).show();
						share.setAddress(address);
						finish();
					}else{
						if(response.getInt("ret") == Constant.NOT_LOGIN){
							LoginUtil login = new LoginUtil(EditAddress.this);
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
				btnSave.setClickable(true);
			}
		}, map);
		new ApplicationUtil(getApplicationContext()).getRequestQueue().add(jsonRequest);
	}

}
