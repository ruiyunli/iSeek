package com.izzz.iseek.base;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.iseek.R;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.dialog.LogDialog;
import com.izzz.iseek.map.CorrectionOverlay;
import com.izzz.iseek.map.MapMKMapViewListener;
import com.izzz.iseek.map.MapOnTouchListener;
import com.izzz.iseek.receiver.SMSreceiver;
import com.izzz.iseek.receiver.SMSsender;
import com.izzz.iseek.setting.SettingActivity;
import com.izzz.iseek.vars.StaticVar;


public class BaseMapMain extends Activity {
	
	public static IseekApplication app = null;
	
	//�ٶȵ�ͼ	
	public static MapView mMapView = null;	
	
	//BroadCastReceiver����ر���
	private SMSreceiver mainReceiver = null;
	private IntentFilter mainFilter = null;
	
	//logDialog�еı���
	public static LogDialog baseDialog;
	
	//�ٶȵ�ͼ	
	public static MapController mMapController = null;
	public static MyLocationOverlay myLocationOverlay = null;
	public static LocationData tarLocData = null;

	
	//У׼�ñ���
	public static CorrectionOverlay correctionOverlay = null;
	public static GeoPoint corrPoint = null;
	public static OverlayItem corrItem = null;
	
	//���������testView 
	public static TextView logText  = null;
	
	//��ͼ�л�
	public static ImageButton btnViewSelect = null; 
	//΢��button
	public static ImageButton btnMoveUp    = null;
	public static ImageButton btnMoveDown  = null;
	public static ImageButton btnMoveLeft  = null;
	public static ImageButton btnMoveRight = null;
	
	//�����˳�ʱ���¼
	long lastTime = 0; 
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		app = (IseekApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            app.mBMapManager.init(StaticVar.BaiduMapKey, new IseekApplication.MyGeneralListener());
        }
		//ע�⣺��������setContentViewǰ��ʼ��BMapManager���󣬷���ᱨ��
        
        //ȥ��������
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //ȥ��״̬��
//        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		
		
		mMapView=(MapView)findViewById(R.id.bmapsView);		
		logText = (TextView)findViewById(R.id.logText);
		baseDialog = new LogDialog(BaseMapMain.this, R.string.DialogMsgHeader, R.string.DialogTitle);
		
		btnViewSelect = (ImageButton)findViewById(R.id.btnViewSelect);
		btnViewSelect.setOnClickListener(new BaseOnClickListener());
		
		InitCorrButton();	//��ʼ��imagebutton����		
		InitBCRRegister();	//ע��BroadCastReceiver IntentFilter
		InitMap();			//�ٶȵ�ͼ��ʼ��			
//		InitDialog();
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "init success");
	}	
	
	//ע��BroadcastReceiver,���ڽ��ն��ż���ִ
	private void InitBCRRegister()
	{		
		mainReceiver = new SMSreceiver();
		mainFilter = new IntentFilter();
		mainFilter.addAction(StaticVar.SYSTEM_SMS_ACTION);
		mainFilter.addAction(StaticVar.COM_SMS_SEND_REFRESH);
		mainFilter.addAction(StaticVar.COM_SMS_DELIVERY_REFRESH);
		mainFilter.addAction(StaticVar.COM_ALARM_REFRESH);
		BaseMapMain.this.registerReceiver(mainReceiver,mainFilter);		
	}
	
	//��ͼ��ʼ������
	private void InitMap()
	{		
		String latitude  = null;
		String longitude = null;
		GeoPoint centerpt = null;
		mMapController = mMapView.getController();
		
		//���ϴα���ĵص�
		latitude  = IseekApplication.prefs.getString(IseekApplication.prefOriginLatitude, "unset");
		longitude = IseekApplication.prefs.getString(IseekApplication.prefOriginLongitude, "unset");
		if((latitude != "unset") &&	(longitude != "unset"))
		{
			//���淽ʽΪ1E6
			centerpt = new GeoPoint(Integer.parseInt(latitude), Integer.parseInt(longitude));
		}
		else
		{
		 centerpt = new GeoPoint((int)(34.128064* 1E6),(int)(108.847287* 1E6));
		}
		 
		mMapController.setCenter(centerpt);
        mMapView.setLongClickable(true);
        mMapController.enableClick(true);        
        mMapController.setZoom(15);        
        mMapView.setBuiltInZoomControls(true);
//      mMapView.setTraffic(true);
        mMapView.setSatellite(true);
        mMapView.setDoubleClickZooming(true);
        
        //MyLocationOverlay��
  		tarLocData = new LocationData();
  		myLocationOverlay = new MyLocationOverlay(mMapView);
  		myLocationOverlay.setData(tarLocData);
  		mMapView.getOverlays().add(myLocationOverlay);
  		myLocationOverlay.enableCompass();
  		mMapView.refresh();		
  		
  		//CorrectionOverlay��
        correctionOverlay = new CorrectionOverlay(getResources().getDrawable(R.drawable.icon_mapselect));
        //MKMapViewListener��Ӧ����
        mMapView.regMapViewListener(IseekApplication.getInstance().mBMapManager, new MapMKMapViewListener(BaseMapMain.this));
        //onTouchListener��Ӧ����
        mMapView.setOnTouchListener(new MapOnTouchListener());
	}
	
	//У׼΢����ť��ʼ��
	public void InitCorrButton()
	{
		//��ȡ
		btnMoveUp    = (ImageButton)findViewById(R.id.btnMoveUp);
		btnMoveDown  = (ImageButton)findViewById(R.id.btnMoveDown);
		btnMoveLeft  = (ImageButton)findViewById(R.id.btnMoveLeft);
		btnMoveRight = (ImageButton)findViewById(R.id.btnMoveRight);
		//��Ӧ����
		btnMoveUp.setOnClickListener(new BaseOnClickListener());
		btnMoveDown.setOnClickListener(new BaseOnClickListener());
		btnMoveLeft.setOnClickListener(new BaseOnClickListener());
		btnMoveRight.setOnClickListener(new BaseOnClickListener());
		//ȡ���ɼ�
		btnMoveUp.setVisibility(View.INVISIBLE);
		btnMoveDown.setVisibility(View.INVISIBLE);
		btnMoveLeft.setVisibility(View.INVISIBLE);
		btnMoveRight.setVisibility(View.INVISIBLE);		
	}
	
	//�����µ�����
	public static void setNewPosition(GeoPoint newPoint)
	{
		//gps����ϵ���ٶ�����ϵ��ת��
		GeoPoint baiduPoint = CoordinateConvert.fromWgs84ToBaidu(newPoint);
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "Latitude:" + baiduPoint.getLatitudeE6() + "  Longitude:" + baiduPoint.getLongitudeE6());
		
		logText.setText("Latitude:" + baiduPoint.getLatitudeE6() + "  Longitude:" + baiduPoint.getLongitudeE6());
		
		tarLocData.latitude  = baiduPoint.getLatitudeE6()/(1E6);
		tarLocData.longitude = baiduPoint.getLongitudeE6()/(1E6);
		tarLocData.accuracy  = (float) 31.181425;
		tarLocData.direction = -1.0f;	
		
		myLocationOverlay.setData(tarLocData);
		mMapController.setZoom(16);
		mMapView.refresh();
		mMapController.animateTo(new GeoPoint((int)(baiduPoint.getLatitudeE6()),(int)(baiduPoint.getLongitudeE6())));
		
	}
		
	private void MenuRefresh()
	{
		//����gpsλ���������
		if(SMSsender.SendMessage(BaseMapMain.this, null, StaticVar.SMS_GEO_REQU, StaticVar.COM_SMS_SEND_REFRESH, 
				StaticVar.COM_SMS_DELIVERY_REFRESH))
		{

			baseDialog.proMessage = (String) getResources().getText(R.string.DialogMsgHeader);
			baseDialog.proLogDialog.setMessage(baseDialog.proMessage);
			baseDialog.enable();
			baseDialog.showLog();
			
			IseekApplication.alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(StaticVar.COM_ALARM_REFRESH);
			IseekApplication.alarmPI = PendingIntent.getBroadcast(this,0,intent,0);
			IseekApplication.alarmManager.set(AlarmManager.RTC_WAKEUP, 
					System.currentTimeMillis() + StaticVar.ALARM_TIME, IseekApplication.alarmPI);
			
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "alarm for refresh set ok!");
		}
	}
		
	private void MenuPhoneCall()
	{
		//����ϵͳ��绰����
		String targetPhone = IseekApplication.prefs.getString(IseekApplication.prefTargetPhoneKey, "unset");
		if(!targetPhone.equals("unset"))
		{
			Intent intent=new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+targetPhone));
			startActivity(intent);
		}
	}
	
	private void MenuCorr(MenuItem item)
	{
		StaticVar.CORRECTION_ENABLE = !StaticVar.CORRECTION_ENABLE;
		if(StaticVar.CORRECTION_ENABLE)
		{
			item.setTitle("UnCorration");
			
			btnMoveUp.setVisibility(View.VISIBLE);
			btnMoveDown.setVisibility(View.VISIBLE);
			btnMoveLeft.setVisibility(View.VISIBLE);
			btnMoveRight.setVisibility(View.VISIBLE);
		}
		else
		{
			item.setTitle("Corration");
			btnMoveUp.setVisibility(View.INVISIBLE);
			btnMoveDown.setVisibility(View.INVISIBLE);
			btnMoveLeft.setVisibility(View.INVISIBLE);
			btnMoveRight.setVisibility(View.INVISIBLE);
			
			//ȥ��У׼��
			mMapView.getOverlays().remove(correctionOverlay);
			mMapView.refresh();
			StaticVar.ADD_LAYER_FLAG = false;
		}
	}
	
	private void MenuSettings()
	{
		Intent intent = new Intent();
		intent.setClass(BaseMapMain.this, SettingActivity.class);
		startActivity(intent);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		//refresh��ť��Ӧ�����Ͷ��ŵ�ָ�����ֻ���
		switch(item.getOrder())
		{
		case StaticVar.MENU_REFRESH:	//ˢ��λ��
			MenuRefresh();
			break;
		
		case StaticVar.MENU_SETTINGS:	//����
			MenuSettings();
			break;
		
		case StaticVar.MENU_EXIT:		//�˳�
			finish();
			break;
			
		case StaticVar.MENU_PHONECALL:	//��绰
			MenuPhoneCall();
			break;
			
		case StaticVar.MENU_TEST:		//����
			GeoPoint newPoint =new GeoPoint((int)(34.235697* 1E6),(int)(108.914238* 1E6));			
			setNewPosition(newPoint);
			break;
			
		case StaticVar.MENU_CORR:		//У׼
			MenuCorr(item);
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
			
	//�ٶȵ�ͼ����
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
	
	//�ٶȵ�ͼ����
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
	
	//�ٶȵ�ͼ����
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

	//3�����������η����˳�Ӧ��
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub		
		
		if(keyCode == KeyEvent.KEYCODE_BACK)		{
			if((System.currentTimeMillis() - lastTime) < 3000)
			{
				return super.onKeyDown(keyCode, event);
			}
			else
				lastTime = System.currentTimeMillis();		
			Toast.makeText(BaseMapMain.this, R.string.ToastExitWarning, Toast.LENGTH_LONG).show();
			return false;
		}
			
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	
}