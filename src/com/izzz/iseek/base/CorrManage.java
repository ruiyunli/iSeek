package com.izzz.iseek.base;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.iseek.R;

public class CorrManage extends ListActivity{

	SimpleAdapter listAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_corr);
		
		
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> map1 = new HashMap<String,String>();
        HashMap<String,String> map2 = new HashMap<String,String>();
        HashMap<String,String> map3 = new HashMap<String,String>();
        HashMap<String,String> map4 = new HashMap<String,String>();
        HashMap<String,String> map5 = new HashMap<String,String>();
        HashMap<String,String> map6 = new HashMap<String,String>();
        HashMap<String,String> map7 = new HashMap<String,String>();
        HashMap<String,String> map8 = new HashMap<String,String>();
        HashMap<String,String> map9 = new HashMap<String,String>();
        map1.put("user_name", "zhangsan");
        map1.put("user_ip", "192.168.0.1");
        map2.put("user_name", "lisi");
        map2.put("user_ip", "192.168.0.2");
        map3.put("user_name", "wangwu");
        map3.put("user_ip", "192.168.0.3");
        map4.put("user_name", "wangwu");
        map4.put("user_ip", "192.168.0.3");
        map5.put("user_name", "wangwu");
        map5.put("user_ip", "192.168.0.3");
        map6.put("user_name", "wangwu");
        map6.put("user_ip", "192.168.0.3");
        map7.put("user_name", "wangwu");
        map7.put("user_ip", "192.168.0.3");
        map8.put("user_name", "wangwu");
        map8.put("user_ip", "192.168.0.3");
        map9.put("user_name", "wangwu");
        map9.put("user_ip", "192.168.0.3");
        
        list.add(map1);
        list.add(map2);
        list.add(map3);
        list.add(map4);
        list.add(map5);
        list.add(map6);
        list.add(map7);
        list.add(map8);
        list.add(map9);
        
        listAdapter = new SimpleAdapter(this,list,R.layout.listview_corr,new String[]{"user_name","user_ip"},new int[]{R.id.corr_origin,R.id.corr_target});
        setListAdapter(listAdapter);
        
    
	}
	
	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		
		boolean isCustom = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //声明使用自定义标题  
		super.setContentView(layoutResID);
		if(isCustom)
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);//自定义布局赋值   
		
		ImageButton btnTitleBar = (ImageButton)findViewById(R.id.btnTitleBar);
		TextView textTitleBar = (TextView)findViewById(R.id.textTitleBar);
		
		//此处修改一下，对应页面的文字标题
		textTitleBar.setText(R.string.TitleBarCorr);
		
		btnTitleBar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

}
