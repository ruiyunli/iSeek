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
	
	//�ٶȵ�ͼ
	BMapManager mBMapMan = null;
	static MapView mMapView = null;	
	
	//���ն���
	private SMSreceiver sMSreceiver = null;
	IntentFilter filter = null;		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mBMapMan=new BMapManager(getApplication());
		mBMapMan.init("3200A096EA79B20773CAB5CBD68C2E1ADDDE22BB", new MyGeneralListener());  
		//ע�⣺��������setContentViewǰ��ʼ��BMapManager���󣬷���ᱨ��
		
		setContentView(R.layout.activity_main);
		
		//ע��BroadcastReceiver,���ڽ��ն���
		sMSreceiver = new SMSreceiver();
		filter = new IntentFilter();
		filter.addAction(StaticVar.SMS_ACTION);
		MainActivity.this.registerReceiver(sMSreceiver,filter);

		//��ȡ�ٶȵ�ͼ�������ļ�
		mMapView=(MapView)findViewById(R.id.bmapsView);
		StaticVar.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		//�ٶȵ�ͼ��ʼ��
		mMapInit();	
		
		//��ʼ��settingҳ��ؼ�����key
		InitPrefKey();
		
		System.out.println("init success");
	}
	
	public void InitPrefKey()
	{
		//��ȡ�ؼ�key�ַ���
		StaticVar.prefTargetPhoneKey = getResources().getString(R.string.set_targetPhone_key);
		StaticVar.prefSosNumberKey   = getResources().getString(R.string.set_sosNumber_key);
		StaticVar.prefSaveAllKey     = getResources().getString(R.string.set_saveall_key);
		StaticVar.prefAboutKey       = getResources().getString(R.string.set_about_key);
	}
	
	//��ͼ��ʼ������
	public void mMapInit()
	{
		//������ʼ��
		StaticVar.tarLocData = new LocationData();
		StaticVar.myLocationOverlay = new MyLocationOverlay(mMapView);
		
		mMapView.setTraffic(true);
		//mMapView.setSatellite(true);
		
		//��ͼ�ĳ�ʼ������
		//�����������õ����ſؼ�
		mMapView.setBuiltInZoomControls(true);		
		// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		
		
		StaticVar.mMapController=mMapView.getController();
		//�ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
		GeoPoint point =new GeoPoint((int)(34.128064* 1E6),(int)(108.847287* 1E6));		
		StaticVar.mMapController.setCenter(point);//���õ�ͼ���ĵ�
		StaticVar.mMapController.setZoom(18);//���õ�ͼzoom����
	}
	
	//MENU��Ӧ����
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		//refresh��ť��Ӧ�����Ͷ��ŵ�ָ�����ֻ���
		if(item.getOrder()== StaticVar.MENU_REFRESH)
		{
			//����gpsλ���������
			StaticVar.SendMessage(MainActivity.this, StaticVar.SMS_GEO_REQU);		       
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
		//���ڲ���
		else if(item.getOrder() == StaticVar.MENU_TEST)
		{
			//���ø���λ��
			StaticVar.setNewPosition(34.235697, 108.914238);
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	
	//�ٶȵ�ͼ����
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
	//�ٶȵ�ͼ����
	@Override
	protected void onPause(){
	        mMapView.onPause();
	        if(mBMapMan!=null){
	                mBMapMan.stop();
	        }
	        super.onPause();
	}
	//�ٶȵ�ͼ����
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
