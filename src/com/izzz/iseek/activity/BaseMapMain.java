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
import com.izzz.iseek.SMS.SMSReceiverBaseMap;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.correction.CorrView;
import com.izzz.iseek.mapcontrol.LocalMapControl;
import com.izzz.iseek.maplistener.MapMKMapViewListener;
import com.izzz.iseek.maplistener.MapOnLongClickListener;
import com.izzz.iseek.maplistener.MapOnTouchListener;
import com.izzz.iseek.maplocate.GPSLocate;
import com.izzz.iseek.maplocate.PhoneLocation;
import com.izzz.iseek.mapplugin.PluginChangeView;
import com.izzz.iseek.mapplugin.PluginZoom;
import com.izzz.iseek.vars.PrefHolder;
import com.izzz.iseek.vars.StaticVar;
import com.izzz.iseek.view.BottomMenu;

public class BaseMapMain extends Activity {

	private IseekApplication app = null;

	private MapView mMapView = null; 				// �ٶȵ�ͼ

	private MapController mMapController = null; 	// �ٶȵ�ͼ
	
	public static GeoPoint gpsPoint		= null;		//gps��λ�İٶ�����
	
	private PhoneLocation mPLocation;				//�ֻ���λ
	
	public static LocalMapControl localMapControl = null;	//���ص�ͼ����
	
	private SMSReceiverBaseMap mainReceiver = null; // BroadCastReceiver����ر���

	private IntentFilter mainFilter = null; // BroadCastReceiver����ر���

	public GPSLocate gpsLocate = null;

	public static CorrView corrView = null; // У����ʵ��

	private ImageButton btnViewSelect = null; // ��ͼ�л�

	private PluginChangeView pluginChangeView = null; // ��ͼ�л����

	private ImageButton btnMoveUp = null; // ΢��button

	private ImageButton btnMoveDown = null; // ΢��button

	private ImageButton btnMoveLeft = null; // ΢��button

	private ImageButton btnMoveRight = null; // ΢��button

	private Button btnCorrOk = null; // У׼��������ť

	private Button btnCorrCancle = null; // У׼��������ť

	private ImageButton btnMenuCall = null; // �˵�

	private Button btnMenuRefresh = null; // �˵�

	private ImageButton btnMenuSettings = null; // �˵�

	private BottomMenu bottomMenu = null; // �˵���ʵ��

	private ImageButton btnZoomIn = null; // zoom��ť

	private ImageButton btnZoomOut = null; // zoom��ť

	private PluginZoom pluginZoom = null; // zoom���

	long lastTime = 0; // �����˳�ʱ���¼

	public static TextView logText = null; // ���������testView

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (IseekApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			app.mBMapManager.init(StaticVar.BaiduMapKey,
					new IseekApplication.MyGeneralListener());
		}
		// ע�⣺��������setContentViewǰ��ʼ��BMapManager���󣬷���ᱨ��

		setContentView(R.layout.activity_main);

		logText = (TextView) findViewById(R.id.logText);

		InitMap();			// �ٶȵ�ͼ��ʼ��
		
//		InitPhoneLocate();	//��λ��ʼ��
		
		InitGPSLocation();	//��ʼ��GPSLocation

		InitCorrection(); 	// ��ʼ��imagebutton����

		InitBottomMenu(); 	// ��ʼ���Զ���˵�

		InitPluginZoom(); 	// ��ʼ�����Ų��

		InitPluginChangeView(); // ��ʼ���л���ͼ���
		
		InitBCRRegister(); 	// ע��BroadCastReceiver IntentFilter

		if (StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "On Create");
	}

	/** ע��BroadcastReceiver,���ڽ��ն��š���ִ������ */
	private void InitBCRRegister() {
		mainReceiver = new SMSReceiverBaseMap(gpsLocate);
		mainFilter = new IntentFilter();
		mainFilter.addAction(StaticVar.SYSTEM_SMS_ACTION);
		mainFilter.addAction(StaticVar.COM_SMS_SEND_REFRESH);
		mainFilter.addAction(StaticVar.COM_SMS_DELIVERY_REFRESH);
		mainFilter.addAction(StaticVar.COM_ALARM_REFRESH);
		mainFilter.setPriority(StaticVar.SMS_RECEIVER_PRIORITY_1);
		BaseMapMain.this.registerReceiver(mainReceiver, mainFilter);
	}

	/** ��ͼ��ʼ������ */
	private void InitMap() {
		String latitude = null;
		String longitude = null;
		GeoPoint centerpt = null;

		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapController = mMapView.getController();

		// ���ϴα���ĵص�
		latitude = PrefHolder.prefs.getString(PrefHolder.prefLastLatitudeKey, "unset");
		longitude = PrefHolder.prefs.getString(PrefHolder.prefLastLongitudeKey, "unset");
		if ((latitude != "unset") && (longitude != "unset")) 
		{
			centerpt = new GeoPoint(Integer.parseInt(latitude),	Integer.parseInt(longitude));	// ���淽ʽΪ1E6
		} 
		else 
		{
			// ����--39.914492,116.403406, ����--34.128064��108.847287
			centerpt = new GeoPoint((int) (39.914492 * 1E6), (int) (116.40340 * 1E6));
		}

		mMapController.setCenter(centerpt);
		mMapView.setLongClickable(false);
		mMapController.enableClick(true);
		mMapController.setZoom(15);
//		mMapView.setBuiltInZoomControls(true);
		mMapView.setTraffic(true);
//		mMapView.setSatellite(true);
		mMapView.setDoubleClickZooming(true);
		
		localMapControl = new LocalMapControl(mMapController);//���ص�ͼ����

		// ע����Ӧ����
		mMapView.regMapViewListener(IseekApplication.getInstance().mBMapManager, new MapMKMapViewListener(BaseMapMain.this));
		
		mMapView.setOnTouchListener(new MapOnTouchListener());		// ע��onTouchListener��Ӧ����
		
		mMapView.setOnLongClickListener(new MapOnLongClickListener(BaseMapMain.this));
		
		mMapView.setLongClickable(false);
		
		StaticVar.logPrint('D', "long clickable: " + mMapView.isLongClickable());

	}
	
	private void InitPhoneLocate()
	{
		mPLocation = ((IseekApplication)getApplication()).mPhoneLocation;
		mPLocation.InitLocationClient();
		mPLocation.Start();
	}
	
	
	/** ��ʼ��GPS��λ��*/
	private void InitGPSLocation()
	{
		gpsLocate = new GPSLocate(BaseMapMain.this, mMapView);
	}

	// У׼΢����ť��ʼ��
	public void InitCorrection() {
		// ��ȡ
		btnCorrOk = (Button) findViewById(R.id.btnCorrOk);
		btnCorrCancle = (Button) findViewById(R.id.btnCorrCancle);
		btnMoveUp = (ImageButton) findViewById(R.id.btnMoveUp);
		btnMoveDown = (ImageButton) findViewById(R.id.btnMoveDown);
		btnMoveLeft = (ImageButton) findViewById(R.id.btnMoveLeft);
		btnMoveRight = (ImageButton) findViewById(R.id.btnMoveRight);

		corrView = new CorrView(BaseMapMain.this, mMapView, btnMoveUp,
				btnMoveDown, btnMoveLeft, btnMoveRight, btnCorrOk, btnCorrCancle);

		corrView.SetAllButtonGone();
	}

	/** ��ʼ���Զ���MenuView */
	private void InitBottomMenu() {
		btnMenuCall = (ImageButton) findViewById(R.id.btnMenuCall);
		btnMenuRefresh = (Button) findViewById(R.id.btnMenuRefresh);
		btnMenuSettings = (ImageButton) findViewById(R.id.btnMenuSettings);

		bottomMenu = new BottomMenu(BaseMapMain.this, btnMenuCall,
				btnMenuRefresh, btnMenuSettings, gpsLocate);
	}

	/** ��ʼ�����Ų�� */
	private void InitPluginZoom() {
		btnZoomIn = (ImageButton) findViewById(R.id.btnZoomIn);
		btnZoomOut = (ImageButton) findViewById(R.id.btnZoomOut);
		pluginZoom = new PluginZoom(mMapView, btnZoomIn, btnZoomOut);
	}

	/** ��ʼ����ͼ�л���� */
	private void InitPluginChangeView() {
		btnViewSelect = (ImageButton) findViewById(R.id.btnViewSelect);
		pluginChangeView = new PluginChangeView(BaseMapMain.this,
				btnViewSelect, mMapView);
	}
	
	// �ٶȵ�ͼ����
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
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

	// �ٶȵ�ͼ����
	@Override
	protected void onPause() {
		mMapView.onPause();
		IseekApplication app = (IseekApplication) this.getApplication();
		if (app.mBMapManager != null) {
			app.mBMapManager.stop();
			app.mBMapManager = null;
		}
		
//		if (StaticVar.DEBUG_ENABLE)
//			StaticVar.logPrint('D', "on Pause");
		
		super.onPause();
	}

	// �ٶȵ�ͼ����
	@Override
	protected void onResume() {
		mMapView.onResume();
		IseekApplication app = (IseekApplication) this.getApplication();
		if (app.mBMapManager != null) {
			app.mBMapManager.start();
			app.mBMapManager = null;
		}

		bottomMenu.SetVisible(true);

//		if (StaticVar.DEBUG_ENABLE)
//			StaticVar.logPrint('D', "on Resume");
		super.onResume();
	}

	// 3�����������η����˳�Ӧ��
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