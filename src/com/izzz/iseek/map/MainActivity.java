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
	
	//�ٶȵ�ͼ	
	static public MapView mMapView = null;	
	
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
	MKMapViewListener mMapListener = null;
		
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
		
		//ע��BroadCastReceiver IntentFilter
		InitBCRRegister();

		//��ȡ�ٶȵ�ͼ�������ļ�
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
		MainActivity.this.registerReceiver(mainReceiver,mainFilter);		
	}
	
	//��ͼ��ʼ������
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
			//���淽ʽΪ1E6
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
        
        //������ʼ��
  		tarLocData = new LocationData();
  		myLocationOverlay = new MyLocationOverlay(mMapView);
  		myLocationOverlay.setData(tarLocData);		
  		mMapView.getOverlays().add(MainActivity.myLocationOverlay);
  		myLocationOverlay.enableCompass();
  		mMapView.refresh();		
  	
        
        
        mMapListener = new MKMapViewListener() {			
    		@Override
    		public void onMapMoveFinish() {
    			// �ڴ˴����ͼ�ƶ������Ϣ�ص�
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
    			// �ڴ˴����ͼ������ɻص�
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
	
	
	
	//���ڳ�ʼ��Dialog��صı���
	private void InitDialog()
	{
		mainLogMessage = (String) getResources().getText(R.string.DialogMsgHeader);
		mainProDialog  = new ProgressDialog(this);		
		mainProDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mainProDialog.setMessage(mainLogMessage);
		mainProDialog.setTitle(getResources().getText(R.string.DialogTitle));
	}
	
	//�����µ�����
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
	
	//MENU��Ӧ����
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		//refresh��ť��Ӧ�����Ͷ��ŵ�ָ�����ֻ���
		if(item.getOrder()== StaticVar.MENU_REFRESH)
		{
			//����gpsλ���������
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
		//���ð�ť��Ӧ
		else if(item.getOrder() == StaticVar.MENU_SETTINGS)
		{
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, SettingActivity.class);
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
	
	// �����¼���������������ͨ�������������Ȩ��֤�����
    class MyGeneralListener implements MKGeneralListener {
        //��������
        @Override
        public void onGetNetworkState(int iError) {
        	//��������
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(MainActivity.this, R.string.ToastErrorInternet,
                    Toast.LENGTH_LONG).show();
            }
            //��������
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(MainActivity.this, R.string.ToastErrorSearchData,
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        //key����
        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //��ȨKey����
                Toast.makeText(MainActivity.this, 
                        R.string.ToastErrorMapKey, Toast.LENGTH_LONG).show();                
            }
        }
    }
}
