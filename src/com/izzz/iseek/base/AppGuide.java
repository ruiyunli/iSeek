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
		
		boolean isCustom = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //����ʹ���Զ������  
		setContentView(R.layout.activity_appguide);
		if(isCustom)
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_guide);//�Զ��岼�ָ�ֵ   
		
		textApp = (TextView)findViewById(R.id.textApp);
		
		textApp.setText(R.string.GuideTextApp);
	}
}
