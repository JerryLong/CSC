package com.guess.user;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

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
import com.android.volley.VolleyError;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditPassword extends Activity implements OnClickListener{
	
	private ShareData share;
	private InputMethodManager imm;
	
	private TextView tvBack, tvTitle;
	
	private EditText etPsdOld, etPsdNew;
	
	private Button btnSave;
	
	private String password;
	
	private String passwordUrl = "http://api.caisichuan.com/api/v1/resetPassword";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_password);;
		
		initView();
	}
	
	/**
	 * 初始化
	 */
	private void initView() {
		share = ShareData.getInstance(getApplicationContext());
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);
		
		etPsdNew = (EditText) findViewById(R.id.edit_psd_new);
		etPsdOld = (EditText) findViewById(R.id.edit_psd_old);
		
		btnSave = (Button) findViewById(R.id.edit_psd_save);
		
		tvBack.setText(R.string.title_back);
		tvTitle.setText(R.string.title_user_passwrod);
		
		tvBack.setOnClickListener(this);
		btnSave.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.navigation_tv_back:
			finish();
			break;

		case R.id.edit_psd_save:
			//修改密码
			if(imm.isActive()){
				imm.hideSoftInputFromWindow(etPsdNew.getApplicationWindowToken(), 0);
			}
			String oldPsd = etPsdOld.getText().toString().trim();
			if(!TextUtils.isEmpty(oldPsd)){
				//网络可用
				if(new MyNetManager(getApplicationContext()).netIsAvailable()){
					password = etPsdNew.getText().toString().trim();
					// 密码检验
					boolean flag = true;
					char[] ch = password.toCharArray();
					if ((ch.length >= 6) && (ch.length <= 16)) {
						for (int i = 0; i < ch.length; i++) {
							if (((ch[i] >= 48) && (ch[i] <= 57))
									|| ((ch[i] >= 65) && (ch[i] <= 90))
									|| ((ch[i] >= 97) && ch[i] <= 122)) {
	
							} else {
								flag = false;
								Toast.makeText(getApplicationContext(),
										getString(R.string.myinfo_password_rule),
										Toast.LENGTH_SHORT).show();
								break;
							}
						}
					} else {
						flag = false;
						Toast.makeText(getApplicationContext(),
								getString(R.string.myinfo_password_rule), Toast.LENGTH_SHORT)
								.show();
					}
					if(flag){//密码格式正确
						if(!oldPsd.equals(password)){
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("oldPassword", oldPsd);
							map.put("newPassword", password);
							updatePsd(passwordUrl, map);
							btnSave.setClickable(false);
						}else{
							Toast.makeText(getApplicationContext(), "新密码和旧密码一致", Toast.LENGTH_SHORT).show();
						}
					}
				}else{
					Toast.makeText(getApplicationContext(),
							getString(R.string.network_not_available),
							Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(getApplicationContext(),
						getString(R.string.info_password_old),
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
		
	}
	
	/**
	 * 修改密码
	 * @param url 服务器地址
	 * @param map 参数
	 */
	private void updatePsd(final String url, final HashMap<String, String> map){
		MyJsonRequest json = new MyJsonRequest(url, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if(response.getInt("ret") == 0){
						Toast.makeText(getApplicationContext(), getString(R.string.info_edit_success), Toast.LENGTH_SHORT).show();
						share.setPhonePassword(password);
						finish();
					}else{
						if(response.getInt("ret") == Constant.NOT_LOGIN){
							LoginUtil login = new LoginUtil(EditPassword.this);
							login.login(new LoginListener() {
								
								@Override
								public void onLoginAfter() {
									updatePsd(url, map);
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
				btnSave.setClickable(true);
				Toast.makeText(getApplicationContext(), getString(R.string.info_edit_defeat), Toast.LENGTH_SHORT).show();
			}
		}, map);
		new ApplicationUtil(getApplicationContext()).getRequestQueue().add(json);
	}

}
