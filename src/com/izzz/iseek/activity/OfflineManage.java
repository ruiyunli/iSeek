package com.izzz.iseek.activity;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKOLSearchRecord;
import com.baidu.mapapi.map.MKOLUpdateElement;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.baidu.mapapi.map.MapController;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.mapcontrol.LocalMapControl;
import com.izzz.iseek.vars.StaticVar;
import com.izzz.iseek.R;

public class OfflineManage extends Activity{
	
	private  LocalMapControl localMapControl = null;
	
	private EditText CityName 			= null;
	
	private Button btnDownload 			= null;
	
	private TextView requestDetail 		= null;
	
	private TextView requestChildCity 	= null;
	
	private Button btnRequest 			= null;
	
	private TextView localSize 			= null;
	
	private TextView localRatio 		= null;
	
	private TextView localUpdate 		= null;
	
	private Button btnUpdate 			= null;
	
	private Button btnDelete 			= null;
	
	private Spinner spinLocalSelecter 	= null;
	
	private ArrayAdapter<String> localAdapter = null;
	
	private int requestCityId = 0;
	
	private String requestCityName;
	
	private int spinIndex = 0; 
	
	private ArrayList<MKOLUpdateElement> updateInfo;
	
	private NotificationManager mNotificationManager;
	
	private NotificationCompat.Builder mBuilder;
	
	private final int mNotiId=1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		IseekApplication app = (IseekApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            app.mBMapManager.init(StaticVar.BaiduMapKey, new IseekApplication.MyGeneralListener());
        }
		setContentView(R.layout.activity_offline);
		
		localMapControl = new LocalMapControl(BaseMapMain.mMapController);
		
		InitView();
		
		UpdateLocal();
	}	
	
	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		
		boolean isCustom = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //����ʹ���Զ������  
		super.setContentView(layoutResID);
		if(isCustom)
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);//�Զ��岼�ָ�ֵ   
		
		ImageButton btnTitleBar = (ImageButton)findViewById(R.id.btnTitleBar);
		TextView textTitleBar = (TextView)findViewById(R.id.textTitleBar);
		
		//�˴��޸�һ�£���Ӧҳ������ֱ���
		textTitleBar.setText(R.string.TitleBarOffline);
		
		btnTitleBar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	private void InitView()
	{
		//�ؼ�
		CityName = (EditText)findViewById(R.id.OffCityName);
		btnDownload = (Button)findViewById(R.id.OffBtnDownload);
		btnRequest = (Button)findViewById(R.id.OffBtnRequest);
		requestDetail = (TextView)findViewById(R.id.OffDetailText);
		requestChildCity = (TextView)findViewById(R.id.OffChildCity);
		localSize = (TextView)findViewById(R.id.OffLocalSize);
		localRatio = (TextView)findViewById(R.id.OffLocalRatio);
		localUpdate = (TextView)findViewById(R.id.OffLocalUpdate); 
		btnUpdate = (Button)findViewById(R.id.OffBtnUpdate);
		btnDelete = (Button)findViewById(R.id.OffBtnDelete);
		spinLocalSelecter = (Spinner)findViewById(R.id.OffLocalSelecter);
				
		//��Ӧ����
		btnRequest.setOnClickListener(new RequestOnClickListener());
		btnDownload.setOnClickListener(new DownloadOnClickListener());
		localMapControl.Init(new OfflineMapListener());
		btnUpdate.setOnClickListener(new UpdateOnClickListener());
		btnDelete.setOnClickListener(new DeleteOnClickListener());
		spinLocalSelecter.setOnItemSelectedListener(new spinnerItemSelectedListener());
		
		btnDownload.setEnabled(false);
	}
	
	private void InitNotification(String cityName)
	{
//		Intent resultIntent = new Intent(this, OfflineManage.class);				
		PendingIntent resultPendingIntent=PendingIntent.getActivity(OfflineManage.this, 0, null, 0);
		
		mBuilder = new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
//		        .setContentTitle(getResources().getString(R.string.app_name))
		        .setContentText(cityName + " " + getResources().getString(R.string.OfflineDownloadEnd))
		        .setAutoCancel(true)
		        .setTicker(getResources().getString(R.string.OfflineDownloadEnd))
		        .setWhen(System.currentTimeMillis())
		        .setVibrate(new long[] {500L, 200L, 200L, 500L})
		        .setContentIntent(resultPendingIntent);
		
		mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	private void SendDownloadNotif(String cityName)
	{
		InitNotification(cityName);
		mNotificationManager.notify(mNotiId, mBuilder.build());
	}
	
	private void UpdateLocal()
	{
		
		updateInfo = localMapControl.getAllUpdateInfo();
		
        if (updateInfo != null) 
        {
        	//ʹ��
        	spinLocalSelecter.setEnabled(true);
        	btnDelete.setEnabled(true);
        	
        	try{
	        	//��ʼ�������ַ���
	        	String[] localMap = new String[updateInfo.size()];
	        	if(StaticVar.DEBUG_ENABLE)
	        		StaticVar.logPrint('D', "info size:" + updateInfo.size());
	        	for ( int i=0; i<updateInfo.size(); i++)
	        	{
	        		localMap[i] = updateInfo.get(i).cityName;    
	        		if(StaticVar.DEBUG_ENABLE)
	        			StaticVar.logPrint('D', "localmap:" + i + "--" + localMap[i]);
	        	}
	        	
	        	//������
	        	localAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,localMap);
	        	localAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        	spinLocalSelecter.setAdapter(localAdapter);
	        	
	        	//��ťʹ��
	        	btnRequest.setEnabled(true);
	        	
        	}catch (Exception e) {
				// TODO: handle exception
        		if(StaticVar.DEBUG_ENABLE)
        			StaticVar.logPrint('E', e.toString());
			} 
        	
        	if(updateInfo.get(0).ratio < 100)
            	IseekApplication.DOWNLOAD_CHANNEL = StaticVar.OFFLINE_UPDATE;
            else
            	IseekApplication.DOWNLOAD_CHANNEL = StaticVar.OFFLINE_NULL;
        }
        else
        {
        	//���spinner
        	localAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
        	localAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        	spinLocalSelecter.setAdapter(localAdapter);
        	//��ʹ��
        	spinLocalSelecter.setEnabled(false);
        	btnUpdate.setEnabled(false);
        	btnDelete.setEnabled(false);
        	//����Ĭ���ַ���
        	localSize.setText(R.string.OfflineTextSelectHint);
        	localRatio.setText(R.string.OfflineTextSelectHint);
        	localUpdate.setText(R.string.OfflineTextSelectHint);
        }
        
        
        
	}
	
	
	//��ѯ��ť��Ӧ����
	class RequestOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub	
			
			//����������б�
			requestChildCity.setText(R.string.OfflineChildCityHint);
			requestDetail.setText(R.string.OfflineDetailHint);
			
			//����Ϊ��
			if("".equals(CityName.getText().toString().trim()))
			{
				Toast.makeText(OfflineManage.this, R.string.OfflineCityNameEmpty, Toast.LENGTH_SHORT).show();
				btnDownload.setEnabled(false);
				return;
			}
			ArrayList<MKOLSearchRecord> records = localMapControl.searchCity(CityName.getText().toString().trim());
			
			//���ղ���
			if(records != null && records.size() == 1)
			{
				MKOLSearchRecord record = records.get(0);
				
				requestCityId = record.cityID;
				
				String recordInfo = record.cityName + String.format("  ��С:%.2fMB  ", ((double)record.size)/1000000);
				
				String StrAppend = getResources().getString(R.string.OfflineRequestDownload);
				btnDownload.setEnabled(true);
				
				if(updateInfo != null)
				{					
					for(MKOLUpdateElement e:updateInfo)
					{
						if(e.cityID == requestCityId)
						{
							//�ɸ���
							if(e.update)
								StrAppend = getResources().getString(R.string.OfflineRequestUpdate);
							//δ�������
							else if(e.ratio < 100)
								StrAppend = e.ratio + "%";
							//���������
							else if(e.ratio == 100)
							{
								StrAppend = getResources().getString(R.string.OfflineRequestNewest);
								btnDownload.setEnabled(false);
							}
							//����
							else
								btnDownload.setEnabled(false);
						}
					}
				}
				
				recordInfo = recordInfo + StrAppend;
				
				requestDetail.setText(recordInfo);
				
				requestCityName = CityName.getText().toString();
				
				//����ǵ�ͼ���������ӵ�ͼ����ʾ�û�
				if(record.childCities != null)
				{
					recordInfo = getResources().getString(R.string.OfflineChildCityHeader);
					for(MKOLSearchRecord e:record.childCities)
					{
						if (StaticVar.DEBUG_ENABLE) 
						{
							StaticVar.logPrint('D', "childCities:" + e.cityName);
						}
						recordInfo = recordInfo + "\t" + e.cityName ;
					}
					requestChildCity.setText(recordInfo);
				}
				
				if(StaticVar.DEBUG_ENABLE)
				{
					StaticVar.logPrint('D', "Array records len:" + records.size());
					StaticVar.logPrint('D', "cityid:" + record.cityID + "cityName:" +record.cityName + 
							"type:" + record.cityType + "size:" + record.size);
				}
			}
			//�鲻����Ӧ��ͼ
			else
			{
				if(StaticVar.DEBUG_ENABLE)
					StaticVar.logPrint('D', "search city error");
				requestDetail.setText(R.string.OfflineRequestNull);
				btnDownload.setEnabled(false);
			}
		}
	}
	
	//���ذ�ť��Ӧ����
	class DownloadOnClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			IseekApplication.DOWNLOAD_CHANNEL = StaticVar.OFFLINE_DOWNLOAD;
			if(localMapControl.download(requestCityId))
			{
				if(!checkNetworkInfo())
				{
					Toast.makeText(OfflineManage.this, R.string.ToastErrorInternet, Toast.LENGTH_SHORT).show();
					return;
				}
				
				requestDetail.setText(requestCityName + ":" + getResources().getString(R.string.OfflineDownloadStart));
				btnDownload.setEnabled(false);
				btnRequest.setEnabled(false);
				
				if(StaticVar.DEBUG_ENABLE)
					StaticVar.logPrint('D', "download start");
					
			}
			else
			{
				IseekApplication.DOWNLOAD_CHANNEL = StaticVar.OFFLINE_NULL;
				if(StaticVar.DEBUG_ENABLE)
					StaticVar.logPrint('D', "download not start");
			}
		}		
	}
	
	//���±��ص�ͼ
	class UpdateOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//����--��ô����
			
			IseekApplication.DOWNLOAD_CHANNEL = StaticVar.OFFLINE_UPDATE;
			if(localMapControl.download(updateInfo.get(spinIndex).cityID))
			{
				
				if(!checkNetworkInfo())
					Toast.makeText(OfflineManage.this, R.string.ToastErrorInternet, Toast.LENGTH_SHORT).show();
				btnUpdate.setEnabled(false);
				
				if(StaticVar.DEBUG_ENABLE)
					StaticVar.logPrint('D', "update start");
			}
			else
			{
				IseekApplication.DOWNLOAD_CHANNEL = StaticVar.OFFLINE_NULL;
				if(StaticVar.DEBUG_ENABLE)
					StaticVar.logPrint('D', "update not start");
			}
		}
	}
	
	//ɾ�����ߵ�ͼ��
	class DeleteOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			new AlertDialog.Builder(OfflineManage.this) 
		    .setTitle(R.string.OfflineAlertDeleteTitle)
		    .setMessage(R.string.OfflineAlertDeleteHint)
		    .setPositiveButton(R.string.OK_CH, new AlertDeleteOnClickListener())
		    .setNegativeButton(R.string.CANCLE_CH, null)
		    .show();
			
		}
	}
	
	//ɾ��ʱ������alertDialog��Ӧ����
	class AlertDeleteOnClickListener implements DialogInterface.OnClickListener
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			String toastStr; 
			if(localMapControl.remove(updateInfo.get(spinIndex).cityID))
				toastStr = getResources().getString(R.string.OfflineDeleteOK);
			else
				toastStr = getResources().getString(R.string.OfflineDeleteFail);
			
			Toast.makeText(OfflineManage.this, toastStr, Toast.LENGTH_SHORT).show();
			
			//������ͼ
			UpdateLocal();
		}
		
	}
	
	
	//spinner��Ӧ
	class spinnerItemSelectedListener implements OnItemSelectedListener
	{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			spinIndex = (int) arg3;
			String sizetmp = String.format("%.2fMB", (double)updateInfo.get(spinIndex).serversize/1000000);
			
			if(StaticVar.DEBUG_ENABLE)
			{
				StaticVar.logPrint('D',"arg2:" + arg2 + "arg3:" + spinIndex);
				StaticVar.logPrint('D', "size:" + sizetmp);
			}
			
			localSize.setText(sizetmp);
			localRatio.setText(updateInfo.get(spinIndex).ratio + "%");
			
			
			//�޸���
			if(updateInfo.get(spinIndex).update)
			{
				if(StaticVar.DEBUG_ENABLE)
					StaticVar.logPrint('D',"get update");
				btnUpdate.setEnabled(true);
				localUpdate.setText(R.string.OfflineTextUpdateAvaliable);
			}
			else if(updateInfo.get(spinIndex).ratio < 100)
			{
				if(StaticVar.DEBUG_ENABLE)
					StaticVar.logPrint('D',"download not done");
				if(updateInfo.get(spinIndex).status != MKOLUpdateElement.DOWNLOADING)
					btnUpdate.setEnabled(true);
				localUpdate.setText(R.string.OfflineTextUpdateAvaliable);
			}
			//�и���
			else
			{
				if(StaticVar.DEBUG_ENABLE)
					StaticVar.logPrint('D',"no update");
				btnUpdate.setEnabled(false);
				localUpdate.setText(R.string.OfflineTextUpdateNotAvaliable);
			}
				
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
	//���ߵ�ͼ��Ӧ����
	class OfflineMapListener implements MKOfflineMapListener{

		@Override
		public void onGetOfflineMapState(int type, int state) {
			// TODO Auto-generated method stub
			
			switch (type) {	
			case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
				{
					MKOLUpdateElement update = localMapControl.getUpdateInfo(state);
					if ( update != null )
					{
						if(IseekApplication.DOWNLOAD_CHANNEL == StaticVar.OFFLINE_DOWNLOAD)
						{
							requestDetail.setText(String.format("%s : %d%%", update.cityName, update.ratio));
							if(StaticVar.DEBUG_ENABLE)
								StaticVar.logPrint('D', String.format("%s : %d%%", update.cityName, update.ratio));
						}
						else if(IseekApplication.DOWNLOAD_CHANNEL == StaticVar.OFFLINE_UPDATE)
						{
							localRatio.setText(String.format("%d%%", update.ratio));
							if(StaticVar.DEBUG_ENABLE)
								StaticVar.logPrint('D', String.format("%s : %d%%", update.cityName, update.ratio));
						}
					}
					
					if(update.ratio == 100)
					{
						//������ͼ
						if(IseekApplication.DOWNLOAD_CHANNEL == StaticVar.OFFLINE_DOWNLOAD)
							requestDetail.setText(R.string.OfflineDownloadEnd);
						UpdateLocal();
						
						SendDownloadNotif(update.cityName);
						
						IseekApplication.DOWNLOAD_CHANNEL = StaticVar.OFFLINE_NULL;
					}
					break;
				}
//			case MKOfflineMap.
				
			}
		}
	}
	
	private boolean checkNetworkInfo()
    {
        ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //mobile 3G Data Network
        State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if(StaticVar.DEBUG_ENABLE)
        	StaticVar.logPrint('D', "mobile:" + mobile.toString());
        //wifi
        State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if(StaticVar.DEBUG_ENABLE)
        	StaticVar.logPrint('D', "wifi:" + wifi.toString());
        
        if((mobile != State.CONNECTED)&&(wifi != State.CONNECTED))
        	return false;
        
        return true;
    }
	
}
