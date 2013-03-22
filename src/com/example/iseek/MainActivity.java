package com.example.iseek;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;


public class MainActivity extends Activity {
	
	//�ٶȵ�ͼ
	BMapManager mBMapMan = null;
	MapView mMapView = null;
	
	//���ն���
	private SMSreceiver sMSreceiver = null;
	private static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	
	//�˵�order
	static final int MENU_REFRESH  = 100;
	static final int MENU_SETTINGS = 200;
	static final int MENU_EXIT     = 300;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mBMapMan=new BMapManager(getApplication());
		mBMapMan.init("3200A096EA79B20773CAB5CBD68C2E1ADDDE22BB", null);  
		//ע�⣺��������setContentViewǰ��ʼ��BMapManager���󣬷���ᱨ��
		
		setContentView(R.layout.activity_main);
		
		//ע��BroadcastReceiver
		sMSreceiver = new SMSreceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SMS_ACTION);
		MainActivity.this.registerReceiver(sMSreceiver,filter);

		mMapView=(MapView)findViewById(R.id.bmapsView);
		
		mMapView.setBuiltInZoomControls(true);		
		//�����������õ����ſؼ�
		MapController mMapController=mMapView.getController();
		// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		GeoPoint point =new GeoPoint((int)(34.234* 1E6),(int)(108.913* 1E6));
		//�ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
		
		mMapController.setCenter(point);//���õ�ͼ���ĵ�
		mMapController.setZoom(16);//���õ�ͼzoom����
	}
	
	@Override
	protected void onDestroy(){
	        mMapView.destroy();
	        if(mBMapMan!=null){
	                mBMapMan.destroy();
	                mBMapMan=null;
	        }
	        super.onDestroy();
	}
	@Override
	protected void onPause(){
	        mMapView.onPause();
	        if(mBMapMan!=null){
	                mBMapMan.stop();
	        }
	        super.onPause();
	}
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
		
		if(item.getOrder()== MENU_REFRESH)
		{
			String strDestAddress = "5554";		     
		    String strMessage = "w000000,051"; 		  
		    SmsManager smsManager = SmsManager.getDefault();		     
		    try 
		    { 
			    PendingIntent mPI = PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(), 0); 
			    smsManager.sendTextMessage(strDestAddress, null, strMessage, mPI, null);
		    } 
		    catch(Exception e) 
		    {
		    	e.printStackTrace();
		    }
		    Toast.makeText(MainActivity.this, "�ͳ��ɹ�!!" , Toast.LENGTH_SHORT).show();		       
		}
		else if(item.getOrder() == MENU_SETTINGS)
		{
			
		}
		else if(item.getOrder() == MENU_EXIT)
		{
			finish();
		}		
		return super.onOptionsItemSelected(item);
	}

}
