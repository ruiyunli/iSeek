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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.baidu.platform.comapi.map.Projection;
import com.example.iseek.R;
import com.izzz.iseek.setting.SettingActivity;
import com.izzz.iseek.sms.SMSreceiver;
import com.izzz.iseek.sms.SMSsender;
import com.izzz.iseek.vars.StaticVar;


public class BaseMapMain extends Activity {
	
	IseekApplication app = null;
	
	//�ٶȵ�ͼ	
	public static MapView mMapView = null;	
	
	//BroadCastReceiver����ر���
	private SMSreceiver mainReceiver = null;
	private IntentFilter mainFilter = null;
	
	//logDialog�еı���
	public static ProgressDialog mainProDialog = null;
	public static String mainLogMessage = null;
	
	//�ٶȵ�ͼ	
	public static MapController mMapController = null;
	public static MyLocationOverlay myLocationOverlay = null;
	public static LocationData tarLocData = null;

	
	//У׼�ñ���
	public static CorrectionOverlay correctionOverlay = null;
	public static GeoPoint corrPoint = null;
	public static OverlayItem corrItem = null;
	
	//��־λ-���У׼��
	public static boolean ADD_LAYER_FLAG = false;
	
	//���������testView
	public static TextView logText  = null;
	public ImageButton btnMoveUp    = null;
	public ImageButton btnMoveDown  = null;
	public ImageButton btnMoveLeft  = null;
	public ImageButton btnMoveRight = null;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		app = (IseekApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            app.mBMapManager.init(StaticVar.BaiduMapKey, new IseekApplication.MyGeneralListener());
        }
		//ע�⣺��������setContentViewǰ��ʼ��BMapManager���󣬷���ᱨ��
		
		setContentView(R.layout.activity_main);
		
		logText = (TextView)findViewById(R.id.logText);
		
		//��ʼ��imagebutton����
		InitCorrButton();
		
		//ע��BroadCastReceiver IntentFilter
		InitBCRRegister();

		//��ȡ�ٶȵ�ͼ
		mMapView=(MapView)findViewById(R.id.bmapsView);		
		
		//�ٶȵ�ͼ��ʼ��
		InitMap();			
		
		//��ʼ��ProgressDialog
		InitDialog();
		
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
	/**
	 * 
	 */
	private void InitMap()
	{		
		String latitude  = null;
		String longitude = null;
		GeoPoint centerpt = null;
		mMapController = mMapView.getController();
		
		//���ϴα���ĵص�
		latitude  = app.prefs.getString(app.prefOriginLatitude, "unset");
		longitude = app.prefs.getString(app.prefOriginLongitude, "unset");
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
        mMapView.displayZoomControls(true);
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
      
        mMapView.regMapViewListener(IseekApplication.getInstance().mBMapManager, new BaseMKMapViewListener(BaseMapMain.this));
        
        mMapView.setOnTouchListener(new BaseOnTouchListener());
	}
	
	//���ڳ�ʼ��Dialog��صı���
	private void InitDialog()
	{
		mainLogMessage = (String) getResources().getText(R.string.DialogMsgHeader);
		mainProDialog  = new ProgressDialog(this);		
		mainProDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mainProDialog.setMessage(mainLogMessage);
		mainProDialog.setTitle(getResources().getText(R.string.DialogTitle));
	}
	
	public void InitCorrButton()
	{
		btnMoveUp    = (ImageButton)findViewById(R.id.btnMoveUp);
		btnMoveDown  = (ImageButton)findViewById(R.id.btnMoveDown);
		btnMoveLeft  = (ImageButton)findViewById(R.id.btnMoveLeft);
		btnMoveRight = (ImageButton)findViewById(R.id.btnMoveRight);

		btnMoveUp.setOnClickListener(new MoveUpListener());
		btnMoveDown.setOnClickListener(new MoveDownListener());
		btnMoveLeft.setOnClickListener(new MoveLeftListener());
		btnMoveRight.setOnClickListener(new MoveRightListener());
		
		btnMoveUp.setVisibility(View.INVISIBLE);
		btnMoveDown.setVisibility(View.INVISIBLE);
		btnMoveLeft.setVisibility(View.INVISIBLE);
		btnMoveRight.setVisibility(View.INVISIBLE);
		
	}
	
	//΢�����ú���
	private void MoveCorration(int direction)
	{
		switch(direction)
		{
		case StaticVar.MOVE_CORR_UP:
			corrPoint.setLatitudeE6(corrPoint.getLatitudeE6() + StaticVar.CORR_STEP);
			break;
		case StaticVar.MOVE_CORR_DOWN:
			corrPoint.setLatitudeE6(corrPoint.getLatitudeE6() - StaticVar.CORR_STEP);
			break;
		case StaticVar.MOVE_CORR_LEFT:
			corrPoint.setLongitudeE6(corrPoint.getLongitudeE6() - StaticVar.CORR_STEP);
			break;
		case StaticVar.MOVE_CORR_RIGHT:
			corrPoint.setLongitudeE6(corrPoint.getLongitudeE6() + StaticVar.CORR_STEP);
			break;
		}
		corrItem =new OverlayItem(corrPoint, "title", "snippet");
		correctionOverlay.refreshItem(corrItem);
		mMapView.refresh();
		logText.setText("x: unknown" + " y: unknown" 
                + '\n' + " latitude: " + corrPoint.getLatitudeE6()
                +" longitude: " + corrPoint.getLongitudeE6());
	}
	//У׼ʱ��΢����ť--����
	class MoveUpListener implements OnClickListener
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			MoveCorration(StaticVar.MOVE_CORR_UP);
		}
		
	}
	//У׼ʱ��΢����ť--����
	class MoveDownListener implements OnClickListener
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			MoveCorration(StaticVar.MOVE_CORR_DOWN);
		}
	}
	//У׼ʱ��΢����ť--����
	class MoveLeftListener implements OnClickListener
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub			
			MoveCorration(StaticVar.MOVE_CORR_LEFT);		
		}
	}
	//У׼ʱ��΢����ť--����
	class MoveRightListener implements OnClickListener
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub			
			MoveCorration(StaticVar.MOVE_CORR_RIGHT);		
		}
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
	
	//MENU��Ӧ����
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		//refresh��ť��Ӧ�����Ͷ��ŵ�ָ�����ֻ���
		if(item.getOrder()== StaticVar.MENU_REFRESH)
		{
			//����gpsλ���������
			if(SMSsender.SendMessage(BaseMapMain.this, null, StaticVar.SMS_GEO_REQU, StaticVar.COM_SMS_SEND_REFRESH, 
					StaticVar.COM_SMS_DELIVERY_REFRESH))
			{
				mainLogMessage = (String) getResources().getText(R.string.DialogMsgHeader);
				mainProDialog.setMessage(mainLogMessage);
				mainProDialog.show();
//				StaticVar.MAIN_DIALOG_ENABLE = true;
				
				IseekApplication.alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
				Intent intent = new Intent(StaticVar.COM_ALARM_REFRESH);
				IseekApplication.alarmPI = PendingIntent.getBroadcast(this,0,intent,0);
				IseekApplication.alarmManager.set(AlarmManager.RTC_WAKEUP, 
						System.currentTimeMillis() + StaticVar.ALARM_TIME, IseekApplication.alarmPI);
				
				if(StaticVar.DEBUG_ENABLE)
					StaticVar.logPrint('D', "alarm for refresh set ok!");
			}
		}
		//���ð�ť��Ӧ
		else if(item.getOrder() == StaticVar.MENU_SETTINGS)
		{
			Intent intent = new Intent();
			intent.setClass(BaseMapMain.this, SettingActivity.class);
			startActivity(intent);
		}
		//�˳���ť��Ӧ
		else if(item.getOrder() == StaticVar.MENU_EXIT)
		{
			finish();
		}
		else if(item.getOrder() == StaticVar.MENU_PHONECALL)
		{
			//����ϵͳ��绰����
			String targetPhone = app.prefs.getString(app.prefTargetPhoneKey, "unset");
			if(!targetPhone.equals("unset"))
			{
				Intent intent=new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+targetPhone));
				startActivity(intent);
			}
		}
		//���ڲ���
		else if(item.getOrder() == StaticVar.MENU_TEST)
		{
			//���ø���λ��
			GeoPoint newPoint =new GeoPoint((int)(34.235697* 1E6),(int)(108.914238* 1E6));			
			
			setNewPosition(newPoint);
		}
		//У׼ʹ��
		else if(item.getOrder() == StaticVar.MENU_CORR)
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
				ADD_LAYER_FLAG = false;
			}
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
}
