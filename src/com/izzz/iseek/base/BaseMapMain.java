package com.izzz.iseek.base;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.iseek.R;
import com.izzz.iseek.SMS.SMSreceiver;
import com.izzz.iseek.SMS.SMSsender;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.map.Correction;
import com.izzz.iseek.map.GPSLocate;
import com.izzz.iseek.map.MapMKMapViewListener;
import com.izzz.iseek.map.MapOnTouchListener;
import com.izzz.iseek.map.PluginChangeView;
import com.izzz.iseek.map.PluginZoom;
import com.izzz.iseek.tools.BottomMenu;
import com.izzz.iseek.tools.LogDialog;
import com.izzz.iseek.vars.StaticVar;

public class BaseMapMain extends Activity {

	private IseekApplication app = null;

	private MapView mMapView = null; // 百度地图

	private SMSreceiver mainReceiver = null; // BroadCastReceiver的相关变量

	private IntentFilter mainFilter = null; // BroadCastReceiver的相关变量

	private MapController mMapController = null; // 百度地图

	public static GPSLocate gpsLocate = null;

	public static Correction correction = null; // 校正类实例

	private ImageButton btnViewSelect = null; // 视图切换

	private PluginChangeView pluginChangeView = null; // 视图切换插件

	private ImageButton btnMoveUp = null; // 微调button

	private ImageButton btnMoveDown = null; // 微调button

	private ImageButton btnMoveLeft = null; // 微调button

	private ImageButton btnMoveRight = null; // 微调button

	private Button btnCorrOk = null; // 校准的两个按钮

	private Button btnCorrCancle = null; // 校准的两个按钮

	private ImageButton btnMenuCall = null; // 菜单

	private Button btnMenuRefresh = null; // 菜单

	private ImageButton btnMenuSettings = null; // 菜单

	private BottomMenu bottomMenu = null; // 菜单类实例

	private ImageButton btnZoomIn = null; // zoom按钮

	private ImageButton btnZoomOut = null; // zoom按钮

	private PluginZoom pluginZoom = null; // zoom插件

	long lastTime = 0; // 用于退出时间记录

//	private SMSsender baseSMSsender = null; // 发送短信接口

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

		InitBCRRegister(); 	// 注册BroadCastReceiver IntentFilter

		InitMap();			// 百度地图初始化
		
		InitGPSLocation();	//初始化GPSLocation

		InitCorrection(); 	// 初始化imagebutton变量

		InitBottomMenu(); 	// 初始化自定义菜单

		InitPluginZoom(); 	// 初始化缩放插件

		InitPluginChangeView(); // 初始化切换视图插件

		if (StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "On Create");
	}

	/** 注册BroadcastReceiver,用于接收短信、回执及闹钟 */
	private void InitBCRRegister() {
		mainReceiver = new SMSreceiver();
		mainFilter = new IntentFilter();
		mainFilter.addAction(StaticVar.SYSTEM_SMS_ACTION);
		mainFilter.addAction(StaticVar.COM_SMS_SEND_REFRESH);
		mainFilter.addAction(StaticVar.COM_SMS_DELIVERY_REFRESH);
		mainFilter.addAction(StaticVar.COM_ALARM_REFRESH);
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
		latitude = IseekApplication.prefs.getString(IseekApplication.prefOriginLatitudeKey, "unset");
		longitude = IseekApplication.prefs.getString(IseekApplication.prefOriginLongitudeKey, "unset");
		if ((latitude != "unset") && (longitude != "unset")) 
		{
			centerpt = new GeoPoint(Integer.parseInt(latitude),	Integer.parseInt(longitude));	// 保存方式为1E6
		} 
		else 
		{
			// 北京--39.914492,116.403406, 西安--34.128064，108.847287
			centerpt = new GeoPoint((int) (39.914492 * 1E6), (int) (116.40340 * 1E6));
		}

		mMapController.setCenter(centerpt);
		mMapView.setLongClickable(true);
		mMapController.enableClick(true);
		mMapController.setZoom(15);
		// mMapView.setBuiltInZoomControls(true);
		mMapView.setTraffic(true);
		// mMapView.setSatellite(true);
		mMapView.setDoubleClickZooming(true);

		// 注册响应函数
		mMapView.regMapViewListener(IseekApplication.getInstance().mBMapManager, new MapMKMapViewListener(BaseMapMain.this));
		// 注册onTouchListener响应函数
		mMapView.setOnTouchListener(new MapOnTouchListener());

	}
	
	/** 初始化GPS定位类*/
	private void InitGPSLocation()
	{
		gpsLocate = new GPSLocate(BaseMapMain.this, mMapView);
	}

	// 校准微调按钮初始化
	public void InitCorrection() {
		// 获取
		btnCorrOk = (Button) findViewById(R.id.btnCorrOk);
		btnCorrCancle = (Button) findViewById(R.id.btnCorrCancle);
		btnMoveUp = (ImageButton) findViewById(R.id.btnMoveUp);
		btnMoveDown = (ImageButton) findViewById(R.id.btnMoveDown);
		btnMoveLeft = (ImageButton) findViewById(R.id.btnMoveLeft);
		btnMoveRight = (ImageButton) findViewById(R.id.btnMoveRight);

		correction = new Correction(BaseMapMain.this, mMapView, btnMoveUp,
				btnMoveDown, btnMoveLeft, btnMoveRight, btnCorrOk, btnCorrCancle);

		correction.SetAllButtonGone();
	}

	/** 初始化自定义MenuView */
	private void InitBottomMenu() {
		btnMenuCall = (ImageButton) findViewById(R.id.btnMenuCall);
		btnMenuRefresh = (Button) findViewById(R.id.btnMenuRefresh);
		btnMenuSettings = (ImageButton) findViewById(R.id.btnMenuSettings);

		bottomMenu = new BottomMenu(BaseMapMain.this, btnMenuCall,
				btnMenuRefresh, btnMenuSettings);
	}

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
		IseekApplication app = (IseekApplication) this.getApplication();
		if (app.mBMapManager != null) {
			app.mBMapManager.destroy();
			app.mBMapManager = null;
		}
		super.onDestroy();
		
		if (StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "on Destroy");
		
		System.exit(0);
	}

	// 百度地图重载
	@Override
	protected void onPause() {
		mMapView.onPause();
		IseekApplication app = (IseekApplication) this.getApplication();
		if (app.mBMapManager != null) {
			app.mBMapManager.stop();
			app.mBMapManager = null;
		}
		
		if (StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "on Pause");
		
		super.onPause();
	}

	// 百度地图重载
	@Override
	protected void onResume() {
		mMapView.onResume();
		IseekApplication app = (IseekApplication) this.getApplication();
		if (app.mBMapManager != null) {
			app.mBMapManager.start();
			app.mBMapManager = null;
		}

		bottomMenu.SetVisible();

		if (StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "on Resume");
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
			GeoPoint newPoint = new GeoPoint((int)(34.128064*(1E6)), (int)(108.847287 * (1E6)));
			gpsLocate.animateTo(newPoint);
		}
		return super.onOptionsItemSelected(item);
	}
}