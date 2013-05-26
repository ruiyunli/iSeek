package com.izzz.iseek.activity;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.izzz.iseek.R;
import com.izzz.iseek.SMS.SMSReceiver;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.mapcontrol.LocalMapControl;
import com.izzz.iseek.maplistener.MapMKMapViewListener;
import com.izzz.iseek.maplocate.GPSLocate;
import com.izzz.iseek.mapplugin.PluginChangeView;
import com.izzz.iseek.mapplugin.PluginZoom;
import com.izzz.iseek.vars.PrefHolder;
import com.izzz.iseek.vars.StaticVar;
import com.izzz.iseek.view.BottomMenu;

public class BaseMapMain extends Activity {

	private IseekApplication app = null;

	private MapView mMapView = null; 				// 百度地图

	public static MapController mMapController = null; 	// 百度地图
	
	private  LocalMapControl localMapControl = null;	//本地地图管理
	
	private SMSReceiver mainReceiver = null; // BroadCastReceiver的相关变量

	private IntentFilter mainFilter = null; // BroadCastReceiver的相关变量

	private GPSLocate gpsLocate = null;

	private ImageButton btnViewSelect = null; // 视图切换

	private PluginChangeView pluginChangeView = null; // 视图切换插件

	private ImageButton btnMenuCall = null; // 菜单

	private Button btnMenuRefresh = null; // 菜单

	private ImageButton btnMenuSettings = null; // 菜单

	private BottomMenu bottomMenu = null; // 菜单类实例

	private ImageButton btnZoomIn = null; // zoom按钮

	private ImageButton btnZoomOut = null; // zoom按钮

	private PluginZoom pluginZoom = null; // zoom插件

	private long lastTime = 0; // 用于退出时间记录

	public static TextView logText = null; // 测试用输出testView

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (IseekApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			app.mBMapManager.init(StaticVar.BaiduMapKey,
					new IseekApplication.MyGeneralListener());
		}
		// 注意：请在试用setContentView前初始化BMapManager对象，否则会报错

		setContentView(R.layout.activity_main);

		logText = (TextView) findViewById(R.id.logText);

		InitMap();			// 百度地图初始化
		
		InitGPSLocation();	//初始化GPSLocation
		
		InitBottomMenu(); 	// 初始化自定义菜单
		
		InitPluginZoom(); 	// 初始化缩放插件

		InitPluginChangeView(); // 初始化切换视图插件
		
		InitBCRRegister(); 	// 注册BroadCastReceiver IntentFilter

		if (StaticVar.DEBUG_ENABLE)
		{
			StaticVar.logPrint('D', "lry:On Create");
		}
	}

	/** 注册BroadcastReceiver,用于接收短信、回执及闹钟 */
	private void InitBCRRegister() {
		mainReceiver = new SMSReceiver(gpsLocate);
		mainFilter = new IntentFilter();
		mainFilter.addAction(StaticVar.SYSTEM_SMS_ACTION);
		mainFilter.addAction(StaticVar.COM_SMS_SEND_REFRESH);
		mainFilter.addAction(StaticVar.COM_SMS_DELIVERY_REFRESH);
		mainFilter.addAction(StaticVar.COM_ALARM_REFRESH);
		mainFilter.setPriority(StaticVar.SMS_RECEIVER_PRIORITY_1);
		BaseMapMain.this.registerReceiver(mainReceiver, mainFilter);
	}

	/** 地图初始化函数 */
	private void InitMap() {
		String latitude = null;
		String longitude = null;
		GeoPoint centerpt = null;

		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapController = mMapView.getController();

		// 打开上次保存的地点
		latitude = PrefHolder.prefs.getString(PrefHolder.prefLastLatitudeKey, "unset");
		longitude = PrefHolder.prefs.getString(PrefHolder.prefLastLongitudeKey, "unset");
		if ((latitude != "unset") && (longitude != "unset")) 
		{
			centerpt = new GeoPoint(Integer.parseInt(latitude),	Integer.parseInt(longitude));	// 保存方式为1E6
		} 
		else 
		{
			// 北京--39.914492,116.403406, 西安--34.128064，108.847287
			centerpt = new GeoPoint((int) (39.914492 * 1E6), (int) (116.40340 * 1E6));
		}
		
		if (StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "init_posi--latitude:" + latitude + " longitude" + longitude);

		mMapController.setCenter(centerpt);
		mMapView.setLongClickable(true);
		mMapController.enableClick(true);
		mMapController.setZoom(15);
//		mMapView.setBuiltInZoomControls(true);
//		mMapView.setTraffic(true);
//		mMapView.setSatellite(true);
		mMapView.setDoubleClickZooming(true);
		
//		localMapControl = new LocalMapControl(mMapController);//本地地图管理

		// 注册响应函数
		mMapView.regMapViewListener(IseekApplication.getInstance().mBMapManager, new MapMKMapViewListener(BaseMapMain.this));
		
	}
	
	
	
		
	/** 初始化GPS定位类*/
	private void InitGPSLocation()
	{
		gpsLocate = new GPSLocate(BaseMapMain.this, mMapView);
	}

	/** 初始化自定义MenuView */
	private void InitBottomMenu() {
		btnMenuCall = (ImageButton) findViewById(R.id.btnMenuCall);
		btnMenuRefresh = (Button) findViewById(R.id.btnMenuRefresh);
		btnMenuSettings = (ImageButton) findViewById(R.id.btnMenuSettings);

		bottomMenu = new BottomMenu(BaseMapMain.this, btnMenuCall,
				btnMenuRefresh, btnMenuSettings, gpsLocate);
	}

	/** 初始化缩放插件 */
	private void InitPluginZoom() {
		btnZoomIn = (ImageButton) findViewById(R.id.btnZoomIn);
		btnZoomOut = (ImageButton) findViewById(R.id.btnZoomOut);
		pluginZoom = new PluginZoom(mMapView, btnZoomIn, btnZoomOut);
	}

	/** 初始化视图切换插件 */
	private void InitPluginChangeView() {
		btnViewSelect = (ImageButton) findViewById(R.id.btnViewSelect);
		pluginChangeView = new PluginChangeView(BaseMapMain.this,
				btnViewSelect, mMapView);
	}
	
		
	// 百度地图重载
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mMapView.destroy();
		IseekApplication app = (IseekApplication) this.getApplication();
		if (app.mBMapManager != null) {
			app.mBMapManager.destroy();
			app.mBMapManager = null;
		}
		
		BaseMapMain.this.unregisterReceiver(mainReceiver);
		
		super.onDestroy();
		
//		if (StaticVar.DEBUG_ENABLE)
//			StaticVar.logPrint('D', "on Destroy");
		
		System.exit(0);
	}

	// 百度地图重载
	@Override
	protected void onPause() {
		mMapView.onPause();
//		IseekApplication app = (IseekApplication) this.getApplication();
//		if (app.mBMapManager != null) {
//			app.mBMapManager.stop();
//			app.mBMapManager = null;
//		}
		
//		if (StaticVar.DEBUG_ENABLE)
//			StaticVar.logPrint('D', "on Pause");
		
		super.onPause();
	}

	// 百度地图重载
	@Override
	protected void onResume() {
		mMapView.onResume();
//		IseekApplication app = (IseekApplication) this.getApplication();
//		if (app.mBMapManager != null) {
//			app.mBMapManager.start();
//			app.mBMapManager = null;
//		}

//		bottomMenu.SetVisible(true);

//		if (StaticVar.DEBUG_ENABLE)
//			StaticVar.logPrint('D', "on Resume");
		super.onResume();
	}

	// 3秒内连按两次返回退出应用
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - lastTime) < 3000) {
				return super.onKeyDown(keyCode, event);
			} else
				lastTime = System.currentTimeMillis();
			Toast.makeText(BaseMapMain.this, R.string.ToastExitWarning,
					Toast.LENGTH_LONG).show();
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		if(item.getOrder() == StaticVar.MENU_TEST)
		{
			mPLocation.requestLocation();
			
		}
		return super.onOptionsItemSelected(item);
	}
	*/
}