package com.example.iseek;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import com.baidu.platform.comapi.basestruct.GeoPoint;


public class MainActivity extends Activity {
	
	//百度地图
	BMapManager mBMapMan = null;
	static MapView mMapView = null;
	static MapController mMapController = null;
	static MyLocationOverlay myLocationOverlay = null;
	static LocationData tarLocData = null;
	
	//接收短信
	private SMSreceiver sMSreceiver = null;
	IntentFilter filter = null;
	private static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	
	//发送短信
	public static final String SMS_GEO_REQU = "w000000,051";//请求位置
	public static final String SMS_SET_SOS = "w000000,003,2,1,";//设置sos号码
	
	//菜单order
	static final int MENU_REFRESH  = 100;
	static final int MENU_SETTINGS = 200;
	static final int MENU_TEST     = 300;
	static final int MENU_EXIT     = 400;
	
	
	//设置存储文件接口
	static SharedPreferences prefs = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mBMapMan=new BMapManager(getApplication());
		mBMapMan.init("3200A096EA79B20773CAB5CBD68C2E1ADDDE22BB", new MyGeneralListener());  
		//注意：请在试用setContentView前初始化BMapManager对象，否则会报错
		
		setContentView(R.layout.activity_main);
		
		//注册BroadcastReceiver,用于接收短信
		sMSreceiver = new SMSreceiver();
		filter = new IntentFilter();
		filter.addAction(SMS_ACTION);
		MainActivity.this.registerReceiver(sMSreceiver,filter);

		//获取百度地图和设置文件
		mMapView=(MapView)findViewById(R.id.bmapsView);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		//百度地图初始化
		mMapInit();		
		
		System.out.println("init success");
	}
	
	
	//MENU响应函数
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		//refresh按钮响应，发送短信到指定的手机号
		if(item.getOrder()== MENU_REFRESH)
		{
			//发送gps位置请求短信
			SendMessage(MainActivity.this, SMS_GEO_REQU);		       
		}
		//设置按钮响应
		else if(item.getOrder() == MENU_SETTINGS)
		{
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, SettingActivity.class);
			startActivity(intent);
		}
		//退出按钮响应
		else if(item.getOrder() == MENU_EXIT)
		{
			finish();
		}
		else if(item.getOrder() == MENU_TEST)
		{
			//设置更新位置
			setNewPosition(34.235697, 108.914238);
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	//地图初始化函数
	public void mMapInit()
	{
		//变量初始化
		tarLocData = new LocationData();
		myLocationOverlay = new MyLocationOverlay(mMapView);
		
		mMapView.setTraffic(true);
		//mMapView.setSatellite(true);
		
		//地图的初始化设置
		//设置启用内置的缩放控件
		mMapView.setBuiltInZoomControls(true);		
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		
		
		mMapController=mMapView.getController();
		//用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		GeoPoint point =new GeoPoint((int)(34.128064* 1E6),(int)(108.847287* 1E6));		
		mMapController.setCenter(point);//设置地图中心点
		mMapController.setZoom(19);//设置地图zoom级别
	}
	
	//短信发送函数
	public static void SendMessage(Context context, String mesContext)
	{
		//获取手机号码		
		
		String strDestAddress = prefs.getString(SettingActivity.prefTargetPhoneKey, "unset");

		if(strDestAddress.equals("unset"))
		{
			Toast.makeText(context, "Please Set Phone Number" , Toast.LENGTH_SHORT).show();
			return ;
		}
		
		//默认指令，gps回传经度和纬度
	    String strMessage = mesContext; 		  
	    SmsManager smsManager = SmsManager.getDefault();		     
	    try 
	    { 
	    	//发送短信
		    PendingIntent mPI = PendingIntent.getBroadcast(context, 0, new Intent(), 0); 
		    smsManager.sendTextMessage(strDestAddress, null, strMessage, mPI, null);
	    } 
	    catch(Exception e) 
	    {
	    	e.printStackTrace();
	    }
	    Toast.makeText(context, "送出成功!!" , Toast.LENGTH_SHORT).show();
	}
	
	
    
	public static void setNewPosition(double Latitude, double Longitude)
	{
		System.out.println("Latitude:" + Latitude + "  Longitude:" + Longitude);
		
		
		tarLocData.latitude = Latitude;
		tarLocData.longitude = Longitude;
		tarLocData.direction = 2.0f;		
			
//		mMapController.setCenter(new GeoPoint((int)(Latitude* 1E6),(int)(Longitude* 1E6)));//设置地图中心点
//		
		
		
		myLocationOverlay.setData(tarLocData);
		mMapView.getOverlays().add(myLocationOverlay);
		mMapView.refresh();
		mMapController.animateTo(new GeoPoint((int)(Latitude* 1E6),(int)(Longitude* 1E6)));
		mMapController.setZoom(18);//设置地图zoom级别
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
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(MainActivity.this, "您的网络出错啦！",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(MainActivity.this, "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //授权Key错误：
                Toast.makeText(MainActivity.this, 
                        "请在 DemoApplication.java文件输入正确的授权Key！", Toast.LENGTH_LONG).show();                
            }
        }
    }
}
