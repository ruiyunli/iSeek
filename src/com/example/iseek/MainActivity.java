package com.example.iseek;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.iseek.setting.SettingActivity;
import com.example.iseek.sms.SMSreceiver;
import com.example.iseek.vars.StaticVar;


public class MainActivity extends Activity {
	
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
		
		IseekApplication app = (IseekApplication)this.getApplication();
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
		
		//��ʼ��settingҳ��ؼ�����key
		InitPrefs();
		
		//��ʼ��ProgressDialog
		InitDialog();
		
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
		MainActivity.this.registerReceiver(mainReceiver,mainFilter);		
	}
	
	//��ͼ��ʼ������
	private void InitMap()
	{		
		mMapController = mMapView.getController();

		GeoPoint centerpt = new GeoPoint((int)(34.128064* 1E6),(int)(108.847287* 1E6));
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
    		}
    		
    		@Override
    		public void onClickMapPoi(MapPoi mapPoiInfo) {
    			String title = "";
    			if (mapPoiInfo != null){
    				title = mapPoiInfo.strText;
    				Toast.makeText(MainActivity.this,title,Toast.LENGTH_SHORT).show();
    				mMapController.animateTo(mapPoiInfo.geoPt);
    			}
    		}		

    		@Override
    		public void onMapAnimationFinish() {
    			// �ڴ˴����ͼ������ɻص�

    		}

    		@Override
    		public void onGetCurrentMap(Bitmap arg0) {
    			// TODO Auto-generated method stub
    			
    		}
    	};
        
        mMapView.regMapViewListener(IseekApplication.getInstance().mBMapManager, mMapListener);
	}
	
	//���ڳ�ʼ��PreferenceActivity�����key
	private void InitPrefs()
	{
		//��ȡ�ؼ�key�ַ���
		StaticVar.prefTargetPhoneKey = getResources().getString(R.string.set_targetPhone_key);
		StaticVar.prefSosNumberKey   = getResources().getString(R.string.set_sosNumber_key);		
		StaticVar.prefAboutKey       = getResources().getString(R.string.set_about_key);
		
		//��ȡprefs��editor
		StaticVar.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		StaticVar.prefsEditor = StaticVar.prefs.edit();
	}
	
	//���ڳ�ʼ��Dialog��صı���
	private void InitDialog()
	{
		mainLogMessage = (String) getResources().getText(R.string.DialogMsgHeader);
		mainProDialog  = new ProgressDialog(this);		
		mainProDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mainProDialog.setMessage(mainLogMessage);
		mainProDialog.setTitle(getResources().getText(R.string.DialogTitle));
		//logDialog.setProgress(0);
		//logDialog.setMax(100);
	}
	
	//MENU��Ӧ����
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		//refresh��ť��Ӧ�����Ͷ��ŵ�ָ�����ֻ���
		if(item.getOrder()== StaticVar.MENU_REFRESH)
		{
			//����gpsλ���������
			if(StaticVar.SendMessage(MainActivity.this, null, StaticVar.SMS_GEO_REQU, StaticVar.COM_SMS_SEND_REFRESH, 
					StaticVar.COM_SMS_DELIVERY_REFRESH))
			{
				mainLogMessage = (String) getResources().getText(R.string.DialogMsgHeader);
				mainProDialog.setMessage(mainLogMessage);
				mainProDialog.show();
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
			String targetPhone = StaticVar.prefs.getString(StaticVar.prefTargetPhoneKey, "unset");
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
			
			StaticVar.setNewPosition(newPoint);
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
