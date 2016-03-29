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
import com.guess.myutils.RandomName;
import com.guess.myutils.ShareData;
import com.guess.myutils.LoginUtil.LoginListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class EditNickName extends Activity implements OnClickListener {
	private InputMethodManager imm;
	private ShareData share;
	private RandomName randomName;

	private String name;

	private TextView tvBack, tvTitle;

	private EditText etName;

	private Button btnRandom, btnSave;

	private String userNameUrl = "http://api.caisichuan.com/user/updateNickname";// 更新昵称

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_name);
		initView();
	}

	/**
	 * 初始化
	 */
	private void initView() {
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		share = ShareData.getInstance(getApplicationContext());
		randomName = new RandomName(getApplicationContext());
		
		tvBack = (TextView) findViewById(R.id.navigation_tv_back);
		tvTitle = (TextView) findViewById(R.id.navigation_tv_title);

		etName = (EditText) findViewById(R.id.edit_et_name);

		btnRandom = (Button) findViewById(R.id.edit_name_random);
		btnSave = (Button) findViewById(R.id.edit_name_save);

		tvBack.setText(R.string.title_back);
		tvTitle.setText(R.string.title_user_name);

		name = share.getNickName();
		etName.setText(name);

		tvBack.setOnClickListener(this);
		btnRandom.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		
		etName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String str = s.toString();
				if(str.length() > 11){
					if(isEmojiEnd(str)){
						etName.setText(str.substring(0, etName.length()-2));
					}else{
						etName.setText(str.substring(0, etName.length()-1));
					}
					etName.setSelection(etName.getText().length());//编辑框光标移到最后一位
				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(etName.getWindowToken(), 0);
		}
		switch (v.getId()) {
		case R.id.navigation_tv_back:
			finish();
			break;

		case R.id.edit_name_random:
			// 随机生成用户名
			etName.setText(randomName.getRandomName());
			break;
		case R.id.edit_name_save:
			// 保存修改
			if (new MyNetManager(getApplicationContext()).netIsAvailable()) {
				String strName = etName.getText().toString().trim();
				if (!strName.equals(name)) {
					if ((strName.length() > 0) && (strName.length() < 11)) {
						name = strName;
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("nickname", strName);
						updateInfo(userNameUrl, map);
						btnSave.setClickable(false);
					} else {
						Toast.makeText(getApplicationContext(), getString(R.string.info_name_lenght),
								Toast.LENGTH_SHORT).show();
					}
				}
			} else {
				Toast.makeText(getApplicationContext(), getString(R.string.network_not_available), Toast.LENGTH_SHORT)
						.show();
			}
			break;
		}

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
						Toast.makeText(getApplicationContext(), getString(R.string.info_edit_success),
								Toast.LENGTH_SHORT).show();
						share.setNickName(name);
						finish();
					} else {
						if(response.getInt("ret") == Constant.NOT_LOGIN){
							LoginUtil login = new LoginUtil(EditNickName.this);
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
	
	/**
     * 检测是否有emoji表情
     *
     * @param source 需要匹配的字符串
     * @return
     */
    public boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }
    /**
     * Emoji表情是否在文本最后
     * @param source
     * @return
     */
    private boolean isEmojiEnd(String source){
    	int len = source.length();     
        char codePoint = source.charAt(len-2);
        if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
            return true;
        }
    	return false;
    }

}
