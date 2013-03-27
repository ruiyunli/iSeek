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
	static MapController mMapController = null;
	static MyLocationOverlay myLocationOverlay = null;
	static LocationData tarLocData = null;
	
	//���ն���
	private SMSreceiver sMSreceiver = null;
	IntentFilter filter = null;
	private static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	
	//���Ͷ���
	public static final String SMS_GEO_REQU = "w000000,051";//����λ��
	public static final String SMS_SET_SOS = "w000000,003,2,1,";//����sos����
	
	//�˵�order
	static final int MENU_REFRESH  = 100;
	static final int MENU_SETTINGS = 200;
	static final int MENU_TEST     = 300;
	static final int MENU_EXIT     = 400;
	
	
	//���ô洢�ļ��ӿ�
	static SharedPreferences prefs = null;
	
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
		filter.addAction(SMS_ACTION);
		MainActivity.this.registerReceiver(sMSreceiver,filter);

		//��ȡ�ٶȵ�ͼ�������ļ�
		mMapView=(MapView)findViewById(R.id.bmapsView);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		//�ٶȵ�ͼ��ʼ��
		mMapInit();		
		
		System.out.println("init success");
	}
	
	
	//MENU��Ӧ����
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		//refresh��ť��Ӧ�����Ͷ��ŵ�ָ�����ֻ���
		if(item.getOrder()== MENU_REFRESH)
		{
			//����gpsλ���������
			SendMessage(MainActivity.this, SMS_GEO_REQU);		       
		}
		//���ð�ť��Ӧ
		else if(item.getOrder() == MENU_SETTINGS)
		{
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, SettingActivity.class);
			startActivity(intent);
		}
		//�˳���ť��Ӧ
		else if(item.getOrder() == MENU_EXIT)
		{
			finish();
		}
		else if(item.getOrder() == MENU_TEST)
		{
			//���ø���λ��
			setNewPosition(34.235697, 108.914238);
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	//��ͼ��ʼ������
	public void mMapInit()
	{
		//������ʼ��
		tarLocData = new LocationData();
		myLocationOverlay = new MyLocationOverlay(mMapView);
		
		mMapView.setTraffic(true);
		//mMapView.setSatellite(true);
		
		//��ͼ�ĳ�ʼ������
		//�����������õ����ſؼ�
		mMapView.setBuiltInZoomControls(true);		
		// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		
		
		mMapController=mMapView.getController();
		//�ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
		GeoPoint point =new GeoPoint((int)(34.128064* 1E6),(int)(108.847287* 1E6));		
		mMapController.setCenter(point);//���õ�ͼ���ĵ�
		mMapController.setZoom(19);//���õ�ͼzoom����
	}
	
	//���ŷ��ͺ���
	public static void SendMessage(Context context, String mesContext)
	{
		//��ȡ�ֻ�����		
		
		String strDestAddress = prefs.getString(SettingActivity.prefTargetPhoneKey, "unset");

		if(strDestAddress.equals("unset"))
		{
			Toast.makeText(context, "Please Set Phone Number" , Toast.LENGTH_SHORT).show();
			return ;
		}
		
		//Ĭ��ָ�gps�ش����Ⱥ�γ��
	    String strMessage = mesContext; 		  
	    SmsManager smsManager = SmsManager.getDefault();		     
	    try 
	    { 
	    	//���Ͷ���
		    PendingIntent mPI = PendingIntent.getBroadcast(context, 0, new Intent(), 0); 
		    smsManager.sendTextMessage(strDestAddress, null, strMessage, mPI, null);
	    } 
	    catch(Exception e) 
	    {
	    	e.printStackTrace();
	    }
	    Toast.makeText(context, "�ͳ��ɹ�!!" , Toast.LENGTH_SHORT).show();
	}
	
	
    
	public static void setNewPosition(double Latitude, double Longitude)
	{
		System.out.println("Latitude:" + Latitude + "  Longitude:" + Longitude);
		
		
		tarLocData.latitude = Latitude;
		tarLocData.longitude = Longitude;
		tarLocData.direction = 2.0f;		
			
//		mMapController.setCenter(new GeoPoint((int)(Latitude* 1E6),(int)(Longitude* 1E6)));//���õ�ͼ���ĵ�
//		
		
		
		myLocationOverlay.setData(tarLocData);
		mMapView.getOverlays().add(myLocationOverlay);
		mMapView.refresh();
		mMapController.animateTo(new GeoPoint((int)(Latitude* 1E6),(int)(Longitude* 1E6)));
		mMapController.setZoom(18);//���õ�ͼzoom����
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
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(MainActivity.this, "���������������",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(MainActivity.this, "������ȷ�ļ���������",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //��ȨKey����
                Toast.makeText(MainActivity.this, 
                        "���� DemoApplication.java�ļ�������ȷ����ȨKey��", Toast.LENGTH_LONG).show();                
            }
        }
    }
}
