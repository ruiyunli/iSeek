package com.izzz.iseek.activity;

import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import com.izzz.iseek.R;
import com.izzz.iseek.vars.PrefHolder;

public class AboutActivity extends Activity{

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_about);
		
		
		
//		PrefHolder.prefsEditor.putStringSet("sdf", new Set<String>("sdf","sdf"));
	}

	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		
		boolean isCustom = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //����ʹ���Զ������  
		super.setContentView(layoutResID);
		if(isCustom)
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);//�Զ��岼�ָ�ֵ   
		
		ImageButton btnTitleBar = (ImageButton)findViewById(R.id.btnTitleBar);
		TextView textTitleBar = (TextView)findViewById(R.id.textTitleBar);
		
		//�˴��޸�һ�£���Ӧҳ������ֱ���
		textTitleBar.setText(R.string.TitleBarAbout);
		
		btnTitleBar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	
	
}
