package com.izzz.iseek.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.example.iseek.R;

public class AppGuide extends Activity{

	private TextView textApp = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		boolean isCustom = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //声明使用自定义标题  
		setContentView(R.layout.activity_appguide);
		if(isCustom)
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_guide);//自定义布局赋值   
		
		textApp = (TextView)findViewById(R.id.textApp);
		
		textApp.setText(R.string.GuideTextApp);
	}
}
