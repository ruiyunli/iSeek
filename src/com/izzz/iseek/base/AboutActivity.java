package com.izzz.iseek.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import com.example.iseek.R;

public class AboutActivity extends Activity{

	private ImageButton btnTitleBarAbout = null; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		boolean isCustom = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //声明使用自定义标题  
		setContentView(R.layout.activity_about);
		if(isCustom)
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_about);//自定义布局赋值   
		
		btnTitleBarAbout = (ImageButton)findViewById(R.id.btnTitleBarAbout);
		
		btnTitleBarAbout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
	}
	
}
