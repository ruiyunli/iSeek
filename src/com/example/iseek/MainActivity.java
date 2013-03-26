package com.example.iseek;

import android.app.Activity;
import android.app.PendingIntent;
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
	private static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	
	//�˵�order
	static final int MENU_REFRESH  = 100;
	static final int MENU_SETTINGS = 200;
	static final int MENU_EXIT     = 300;
	static final int MENU_TEST     = 400;
	
	//���ô洢�ļ��ӿ�
	SharedPreferences prefs = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mBMapMan=new BMapManager(getApplication());
		mBMapMan.init("3200A096EA79B20773CAB5CBD68C2E1ADDDE22BB", new MyGeneralListener());  
		//ע�⣺��������setContentViewǰ��ʼ��BMapManager���󣬷���ᱨ��
		
		setContentView(R.layout.activity_main);
		
		//ע��BroadcastReceiver,���ڽ��ն���
		sMSreceiver = new SMSreceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SMS_ACTION);
		MainActivity.this.registerReceiver(sMSreceiver,filter);

		mMapView=(MapView)findViewById(R.id.bmapsView);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		//������ʼ��
		tarLocData = new LocationData();
		myLocationOverlay = new MyLocationOverlay(mMapView);
		
		//��ͼ�ĳ�ʼ������
		//�����������õ����ſؼ�
		mMapView.setBuiltInZoomControls(true);		
		// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		mMapController=mMapView.getController();
		//�ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
		GeoPoint point =new GeoPoint((int)(34.128064* 1E6),(int)(108.847287* 1E6));		
		mMapController.setCenter(point);//���õ�ͼ���ĵ�
		mMapController.setZoom(19);//���õ�ͼzoom����
		
		System.out.println("init success");
	}
	
	//�ٶȵ�ͼ����
	@Override
	protected void onDestroy(){
	        mMapView.destroy();
	        if(mBMapMan!=null){
	                mBMapMan.destroy();
	                mBMapMan=null;
	        }
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		//refresh��ť��Ӧ�����Ͷ��ŵ�ָ�����ֻ���
		if(item.getOrder()== MENU_REFRESH)
		{
			//��ȡ�ֻ�����			
			String strDestAddress = prefs.getString("targetPhone", "unset");

			if(strDestAddress.equals("unset"))
			{
				Toast.makeText(MainActivity.this, "Please Set Phone Number" , Toast.LENGTH_SHORT).show();
				return super.onOptionsItemSelected(item);
			}
			
			//Ĭ��ָ�gps�ش����Ⱥ�γ��
		    String strMessage = "w000000,051"; 		  
		    SmsManager smsManager = SmsManager.getDefault();		     
		    try 
		    { 
		    	//���Ͷ���
			    PendingIntent mPI = PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(), 0); 
			    smsManager.sendTextMessage(strDestAddress, null, strMessage, mPI, null);
		    } 
		    catch(Exception e) 
		    {
		    	e.printStackTrace();
		    }
		    Toast.makeText(MainActivity.this, "�ͳ��ɹ�!!" , Toast.LENGTH_SHORT).show();		       
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
			setNewPosition(34.235697, 108.914238);
		}
		return super.onOptionsItemSelected(item);
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
    
	public static void setNewPosition(double Latitude, double Longitude)
	{
		System.out.println("Latitude:" + Latitude + "  Longitude:" + Longitude);
		
		
		tarLocData.latitude = Latitude;
		tarLocData.longitude = Longitude;
		tarLocData.direction = 2.0f;		
			
//		mMapController.setCenter(new GeoPoint((int)(Latitude* 1E6),(int)(Longitude* 1E6)));//���õ�ͼ���ĵ�
//		mMapController.setZoom(19);//���õ�ͼzoom����
		
		
		myLocationOverlay.setData(tarLocData);
		mMapView.getOverlays().add(myLocationOverlay);
		mMapView.refresh();
		mMapController.animateTo(new GeoPoint((int)(Latitude* 1E6),(int)(Longitude* 1E6)));
		
	}

}
