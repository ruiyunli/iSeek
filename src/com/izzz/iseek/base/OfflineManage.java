package com.izzz.iseek.base;


import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mapapi.map.MKOLSearchRecord;
import com.baidu.mapapi.map.MKOLUpdateElement;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.example.iseek.R;
import com.izzz.iseek.vars.StaticVar;

public class OfflineManage extends Activity{
	
	private EditText CityName = null;
	
	private Button btnDownload = null;
	
	private TextView TextDetail = null;
	
	private Button btnRequest = null;
	
	int requestCityId = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		boolean isCustom = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //声明使用自定义标题  
		setContentView(R.layout.activity_offline);
		if(isCustom)
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_offline);//自定义布局赋值
		
		//控件
		CityName = (EditText)findViewById(R.id.OffCityName);
		btnDownload = (Button)findViewById(R.id.OffDownload);
		btnRequest = (Button)findViewById(R.id.OffRequest);
		TextDetail = (TextView)findViewById(R.id.OffDetailText);
				
		//响应函数
		btnRequest.setOnClickListener(new RequestOnClickListener());
		btnDownload.setOnClickListener(new DownloadOnClickListener());
		BaseMapMain.localMapControl.Init(new OfflineMapListener());
		
	}	
	
	
	
	//查询按钮响应函数
	class RequestOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub			
			
			ArrayList<MKOLSearchRecord> records = BaseMapMain.localMapControl.searchCity(CityName.getText().toString());
			if (records == null || records.size() != 1)
			{
				if(StaticVar.DEBUG_ENABLE)
					StaticVar.logPrint('D', "search city error");
				return;
			}
			requestCityId = records.get(0).cityID;
			TextDetail.setText(String.valueOf(requestCityId));
		}
	}
	
	//下载按钮响应函数
	class DownloadOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			if(BaseMapMain.localMapControl.download(requestCityId))
			{
				if(StaticVar.DEBUG_ENABLE)
					StaticVar.logPrint('D', "download start");
			}
			else
			{
				if(StaticVar.DEBUG_ENABLE)
					StaticVar.logPrint('D', "download not start");
			}
		}		
	}
	
	//离线地图响应函数
		class OfflineMapListener implements MKOfflineMapListener{

			@Override
			public void onGetOfflineMapState(int type, int state) {
				// TODO Auto-generated method stub
				
				switch (type) {	
				case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
					{
						// Log.d("OfflineDemo", String.format("cityid:%d update", state));
						MKOLUpdateElement update = BaseMapMain.localMapControl.getUpdateInfo(state);
						if ( update != null )
						    TextDetail.setText(String.format("%s : %d%%", update.cityName, update.ratio));
					}
					break;
				}
				
			}
		}
	
}
