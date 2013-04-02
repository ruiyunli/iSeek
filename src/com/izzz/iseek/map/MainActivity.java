package com.izzz.iseek.map;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.iseek.R;
import com.izzz.iseek.setting.SettingActivity;
import com.izzz.iseek.sms.SMSreceiver;
import com.izzz.iseek.sms.SMSsender;
import com.izzz.iseek.vars.StaticVar;


public class MainActivity extends Activity {
	
	IseekApplication app = null;
	
	//百度地图	
	static public MapView mMapView = null;	
	
	//BroadCastReceiver的相关变量
	private SMSreceiver mainReceiver = null;
	private IntentFilter mainFilter = null;
	
	//logDialog中的变量
	public static ProgressDialog mainProDialog = null;
	public static String mainLogMessage = null;
	
	//百度地图	
	public static MapController mMapController = null;
	public static MyLocationOverlay myLocationOverlay = null;
	public static LocationData tarLocData = null;
	MKMapViewListener mMapListener = null;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		app = (IseekApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            app.mBMapManager.init(StaticVar.BaiduMapKey, new IseekApplication.MyGeneralListener());
        }
		//注意：请在试用setContentView前初始化BMapManager对象，否则会报错
		
		setContentView(R.layout.activity_main);
		
		//注册BroadCastReceiver IntentFilter
		InitBCRRegister();

		//获取百度地图和设置文件
		mMapView=(MapView)findViewById(R.id.bmapsView);		
		
		//百度地图初始化
		InitMap();			
		
		//初始化ProgressDialog
		InitDialog();
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "init success");
	}	
	
	//注册BroadcastReceiver,用于接收短信及回执
	private void InitBCRRegister()
	{		
		mainReceiver = new SMSreceiver();
		mainFilter = new IntentFilter();
		mainFilter.addAction(StaticVar.SYSTEM_SMS_ACTION);
		mainFilter.addAction(StaticVar.COM_SMS_SEND_REFRESH);
		mainFilter.addAction(StaticVar.COM_SMS_DELIVERY_REFRESH);
		mainFilter.addAction(StaticVar.COM_ALARM_REFRESH);
		MainActivity.this.registerReceiver(mainReceiver,mainFilter);		
	}
	
	//地图初始化函数
	private void InitMap()
	{		
		String latitude  = null;
		String longitude = null;
		GeoPoint centerpt = null;
		mMapController = mMapView.getController();
		
		latitude  = app.prefs.getString(app.prefOriginLatitude, "unset");
		longitude = app.prefs.getString(app.prefOriginLongitude, "unset");
		if((latitude != "unset") &&	(longitude != "unset"))
		{
			//保存方式为1E6
			centerpt = new GeoPoint(Integer.parseInt(latitude), Integer.parseInt(longitude));
		}
		else
		{
		 centerpt = new GeoPoint((int)(34.128064* 1E6),(int)(108.847287* 1E6));
		}
		 
		mMapController.setCenter(centerpt);
        mMapView.setLongClickable(true);
        //mMapController.setMapClickEnable(true);
       // mMapView.setSatellite(false);       
        mMapController.enableClick(true);
        
        mMapController.setZoom(12);        
        mMapView.displayZoomControls(true);
//        mMapView.setTraffic(true);
        mMapView.setSatellite(true);
        mMapView.setDoubleClickZooming(true);
        mMapView.setOnTouchListener(null);
        
        //变量初始化
  		tarLocData = new LocationData();
  		myLocationOverlay = new MyLocationOverlay(mMapView);
  		myLocationOverlay.setData(tarLocData);		
  		mMapView.getOverlays().add(MainActivity.myLocationOverlay);
  		myLocationOverlay.enableCompass();
  		mMapView.refresh();		
  	
        
        
        mMapListener = new MKMapViewListener() {			
    		@Override
    		public void onMapMoveFinish() {
    			// 在此处理地图移动完成消息回调
    			if(StaticVar.DEBUG_ENABLE)
    				StaticVar.logPrint('D', "onMapMoveFinish()");
    		}
    		
    		@Override
    		public void onClickMapPoi(MapPoi mapPoiInfo) {
    			String title = "";
    			if(StaticVar.DEBUG_ENABLE)
    				StaticVar.logPrint('D', "onClickMapPoi()");
    			if (mapPoiInfo != null){
    				title = mapPoiInfo.strText + "--Lat:" + mapPoiInfo.geoPt.getLatitudeE6() + " Lon:" +mapPoiInfo.geoPt.getLongitudeE6();
    				Toast.makeText(MainActivity.this,title,Toast.LENGTH_LONG).show();
    				mMapController.animateTo(mapPoiInfo.geoPt);
    			}
    		}		

    		@Override
    		public void onMapAnimationFinish() {
    			// 在此处理地图动画完成回调
    			if(StaticVar.DEBUG_ENABLE)
    				StaticVar.logPrint('D', "onMapAnimationFinish()");

    		}

    		@Override
    		public void onGetCurrentMap(Bitmap arg0) {
    			// TODO Auto-generated method stub
    			
    		}
    	};
        
        mMapView.regMapViewListener(IseekApplication.getInstance().mBMapManager, mMapListener);
	}
	
	
	
	//用于初始化Dialog相关的变量
	private void InitDialog()
	{
		mainLogMessage = (String) getResources().getText(R.string.DialogMsgHeader);
		mainProDialog  = new ProgressDialog(this);		
		mainProDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mainProDialog.setMessage(mainLogMessage);
		mainProDialog.setTitle(getResources().getText(R.string.DialogTitle));
	}
	
	//设置新的坐标
	public static void setNewPosition(GeoPoint newPoint)
	{
		
		GeoPoint baiduPoint = CoordinateConvert.fromWgs84ToBaidu(newPoint);
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "Latitude:" + baiduPoint.getLatitudeE6() + "  Longitude:" + baiduPoint.getLongitudeE6());
		
		
		tarLocData.latitude  = baiduPoint.getLatitudeE6()/(1E6);
		tarLocData.longitude = baiduPoint.getLongitudeE6()/(1E6);
		tarLocData.accuracy  = (float) 31.181425;
		tarLocData.direction = -1.0f;	
		
		myLocationOverlay.setData(MainActivity.tarLocData);
		mMapController.setZoom(12);
		mMapView.refresh();
		mMapController.animateTo(new GeoPoint((int)(baiduPoint.getLatitudeE6()),(int)(baiduPoint.getLongitudeE6())));
		
	}
	
	//MENU响应函数
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		//refresh按钮响应，发送短信到指定的手机号
		if(item.getOrder()== StaticVar.MENU_REFRESH)
		{
			//发送gps位置请求短信
			if(SMSsender.SendMessage(MainActivity.this, null, StaticVar.SMS_GEO_REQU, StaticVar.COM_SMS_SEND_REFRESH, 
					StaticVar.COM_SMS_DELIVERY_REFRESH))
			{
				mainLogMessage = (String) getResources().getText(R.string.DialogMsgHeader);
				mainProDialog.setMessage(mainLogMessage);
				mainProDialog.show();
				
				IseekApplication.alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
				Intent intent = new Intent(StaticVar.COM_ALARM_REFRESH);
				IseekApplication.alarmPI = PendingIntent.getBroadcast(this,0,intent,0);
				IseekApplication.alarmManager.set(AlarmManager.RTC_WAKEUP, 
						System.currentTimeMillis() + StaticVar.ALARM_TIME, IseekApplication.alarmPI);
				
				if(StaticVar.DEBUG_ENABLE)
					StaticVar.logPrint('D', "alarm for refresh set ok!");
			}
		}
		//设置按钮响应
		else if(item.getOrder() == StaticVar.MENU_SETTINGS)
		{
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, SettingActivity.class);
			startActivity(intent);
		}
		//退出按钮响应
		else if(item.getOrder() == StaticVar.MENU_EXIT)
		{
			finish();
		}
		else if(item.getOrder() == StaticVar.MENU_PHONECALL)
		{
			//调用系统打电话程序
			String targetPhone = app.prefs.getString(app.prefTargetPhoneKey, "unset");
			if(!targetPhone.equals("unset"))
			{
				Intent intent=new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+targetPhone));
				startActivity(intent);
			}
		}
		//用于测试
		else if(item.getOrder() == StaticVar.MENU_TEST)
		{
			//设置更新位置
			GeoPoint newPoint =new GeoPoint((int)(34.235697* 1E6),(int)(108.914238* 1E6));			
			
			setNewPosition(newPoint);
		}
		return super.onOptionsItemSelected(item);
	}
			
	//百度地图重载
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		IseekApplication app = (IseekApplication)this.getApplication();
		if (app.mBMapManager != null) {
			app.mBMapManager.destroy();
			app.mBMapManager = null;
		}
		super.onDestroy();
		System.exit(0);
	}
	
	//百度地图重载
	@Override
	protected void onPause(){
	        mMapView.onPause();
	        IseekApplication app = (IseekApplication)this.getApplication();
			if (app.mBMapManager != null) {
				app.mBMapManager.stop();
				app.mBMapManager = null;
			}
	        super.onPause();
	}
	
	//百度地图重载
	@Override
	protected void onResume(){
	        mMapView.onResume();
	        IseekApplication app = (IseekApplication)this.getApplication();
			if (app.mBMapManager != null) {
				app.mBMapManager.start();
				app.mBMapManager = null;
			}
	        super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    class MyGeneralListener implements MKGeneralListener {
        //网络问题
        @Override
        public void onGetNetworkState(int iError) {
        	//网络连接
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(MainActivity.this, R.string.ToastErrorInternet,
                    Toast.LENGTH_LONG).show();
            }
            //检索内容
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(MainActivity.this, R.string.ToastErrorSearchData,
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        //key问题
        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //授权Key错误：
                Toast.makeText(MainActivity.this, 
                        R.string.ToastErrorMapKey, Toast.LENGTH_LONG).show();                
            }
        }
    }
}
