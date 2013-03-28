package com.example.iseek;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.utils.CoordinateConver;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.iseek.sms.SMSreceiver;
import com.example.iseek.ui.SettingActivity;
import com.example.iseek.vars.StaticVar;


public class MainActivity extends Activity {
	
	//百度地图
	BMapManager mBMapMan = null;
	static public MapView mMapView = null;	
	
	//BroadCastReceiver的相关变量
	private SMSreceiver sMSreceiver = null;
	IntentFilter filter = null;	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mBMapMan=new BMapManager(getApplication());
		mBMapMan.init(StaticVar.BaiduMapKey, new MyGeneralListener());  
		//注意：请在试用setContentView前初始化BMapManager对象，否则会报错
		
		setContentView(R.layout.activity_main);
		
		//注册BroadCastReceiver IntentFilter
		InitBCRRegister();

		//获取百度地图和设置文件
		mMapView=(MapView)findViewById(R.id.bmapsView);
		StaticVar.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		//百度地图初始化
		InitMap();	
		
		//初始化setting页面控件索引key
		InitPrefKey();
		
		//初始化ProgressDialog
		InitDialog();
		
		System.out.println("init success");
	}	
	
	//注册BroadcastReceiver,用于接收短信及回执
	private void InitBCRRegister()
	{		
		sMSreceiver = new SMSreceiver();
		filter = new IntentFilter();
		filter.addAction(StaticVar.SYSTEM_SMS_ACTION);
		filter.addAction(StaticVar.COM_SMS_SEND);		//发送状态
		filter.addAction(StaticVar.COM_SMS_DELIVERY);	//接收回执状态
		MainActivity.this.registerReceiver(sMSreceiver,filter);
	}
	
	//地图初始化函数
	private void InitMap()
	{
		//变量初始化
		StaticVar.tarLocData = new LocationData();
		StaticVar.myLocationOverlay = new MyLocationOverlay(mMapView);
		
		mMapView.setTraffic(true);
		//mMapView.setSatellite(true);
		
		//地图的初始化设置
		//设置启用内置的缩放控件
		mMapView.setBuiltInZoomControls(true);		
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		
		
		StaticVar.mMapController=mMapView.getController();
		//用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		GeoPoint point =new GeoPoint((int)(34.128064* 1E6),(int)(108.847287* 1E6));		
		StaticVar.mMapController.setCenter(point);//设置地图中心点
		StaticVar.mMapController.setZoom(18);//设置地图zoom级别
	}
	
	//用于初始化PreferenceActivity的相关key
	private void InitPrefKey()
	{
		//获取控件key字符串
		StaticVar.prefTargetPhoneKey = getResources().getString(R.string.set_targetPhone_key);
		StaticVar.prefSosNumberKey   = getResources().getString(R.string.set_sosNumber_key);
		StaticVar.prefSaveAllKey     = getResources().getString(R.string.set_saveall_key);
		StaticVar.prefAboutKey       = getResources().getString(R.string.set_about_key);
	}
	
	//用于初始化Dialog相关的变量
	private void InitDialog()
	{
		StaticVar.logMessage = new String("");
		StaticVar.logDialog = new ProgressDialog(this);		
		StaticVar.logDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		StaticVar.logDialog.setMessage(getResources().getText(R.string.DialogMsgHeader));
		StaticVar.logDialog.setTitle(getResources().getText(R.string.DialogTitle));
		//logDialog.setProgress(0);
		//logDialog.setMax(100);
	}
	
	//MENU响应函数
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		//refresh按钮响应，发送短信到指定的手机号
		if(item.getOrder()== StaticVar.MENU_REFRESH)
		{
			//发送gps位置请求短信
			StaticVar.logMessage = (String) getResources().getText(R.string.DialogMsgHeader);
			StaticVar.SendMessage(MainActivity.this, StaticVar.SMS_GEO_REQU);
			StaticVar.logDialog.show();
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
			String targetPhone = StaticVar.prefs.getString(StaticVar.prefTargetPhoneKey, "unset");
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
			GeoPoint newPoint2 = CoordinateConver.fromWgs84ToBaidu(newPoint);
			
			StaticVar.setNewPosition(newPoint2.getLatitudeE6()/(1E6), newPoint2.getLongitudeE6()/(1E6));
		}
		return super.onOptionsItemSelected(item);
	}
			
	//百度地图重载
	@Override
	protected void onDestroy(){
		mMapView.destroy();
	        if(mBMapMan!=null){
	                mBMapMan.destroy();
	                mBMapMan=null;
	        }	        
	        MainActivity.this.unregisterReceiver(sMSreceiver);
	        super.onDestroy();
	}
	
	//百度地图重载
	@Override
	protected void onPause(){
	        mMapView.onPause();
	        if(mBMapMan!=null){
	                mBMapMan.stop();
	        }
	        super.onPause();
	}
	
	//百度地图重载
	@Override
	protected void onResume(){
	        mMapView.onResume();
	        if(mBMapMan!=null){
	                mBMapMan.start();
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
