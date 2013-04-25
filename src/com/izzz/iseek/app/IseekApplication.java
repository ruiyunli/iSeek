package com.izzz.iseek.app;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.example.iseek.R;
import com.izzz.iseek.vars.StaticVar;


import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class IseekApplication extends Application {

	private static IseekApplication mInstance = null;	//��ͼ���
	
    public boolean m_bKeyRight = true;					//��ͼ���
    
    public BMapManager mBMapManager = null;				//��ͼ���
    
  	public static String prefTargetPhoneKey  	= null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static String prefSosNumberKey    	= null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static String prefCorrEntryKey     	= null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static String prefCorrEnableKey		= null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static String prefOfflineKey			= null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static String prefGuideKey			= null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static String prefAboutKey        	= null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static String prefOriginLatitudeKey  = null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static String prefOriginLongitudeKey = null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static SharedPreferences prefs    	= null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static SharedPreferences.Editor prefsEditor = null;	//�ؼ���Ӧ��key�ַ�������
  	
  	public static int DOWNLOAD_CHANNEL = StaticVar.OFFLINE_NULL;	//��������ҳ�������;����־
  	
  	public static boolean CORRECTION_ENABLE = false;
  	
  	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		//thisָ��
		mInstance = this;
		
		//�ٶȵ�ͼBMapManger����
		initEngineManager(this);
		
		//��ʼ��shareedPreference
		InitPrefs();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		if (mBMapManager != null) {
            mBMapManager.destroy();
            mBMapManager = null;
        }
		super.onTerminate();
	}
	
	public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(StaticVar.BaiduMapKey,new MyGeneralListener())) {
            Toast.makeText(IseekApplication.getInstance().getApplicationContext(), 
                    "BMapManager  ��ʼ������!", Toast.LENGTH_LONG).show();
        }
	}
	
	//���ڳ�ʼ��PreferenceActivity�����key
	private void InitPrefs()
	{
		//��ȡ�ؼ�key�ַ���
		prefTargetPhoneKey  	= getResources().getString(R.string.set_targetPhone_key);
		prefSosNumberKey    	= getResources().getString(R.string.set_sosNumber_key);
		prefCorrEntryKey   		= getResources().getString(R.string.set_correntry_key);
		prefOfflineKey			= getResources().getString(R.string.set_offline_key);
		prefGuideKey			= getResources().getString(R.string.set_guide_key);
		prefCorrEnableKey 		= getResources().getString(R.string.set_correnable_key);
		prefAboutKey        	= getResources().getString(R.string.set_about_key);
		prefOriginLatitudeKey  	= getResources().getString(R.string.set_origin_latitude_key);
		prefOriginLongitudeKey 	= getResources().getString(R.string.set_origin_longitude_key);
		
		//��ȡprefs��editor
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefsEditor = prefs.edit();
		
		CORRECTION_ENABLE = prefs.getBoolean(prefCorrEnableKey,false); 
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "correction enable:" + CORRECTION_ENABLE);
	}
	
	public static IseekApplication getInstance() {
		return mInstance;
	}
	
	// �����¼���������������ͨ�������������Ȩ��֤�����
    public static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(IseekApplication.getInstance().getApplicationContext(), "���������������",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(IseekApplication.getInstance().getApplicationContext(), "������ȷ�ļ���������",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //��ȨKey����
                Toast.makeText(IseekApplication.getInstance().getApplicationContext(), 
                        "���� DemoApplication.java�ļ�������ȷ����ȨKey��", Toast.LENGTH_LONG).show();
                IseekApplication.getInstance().m_bKeyRight = false;
            }
        }
    }
	
	
	
	

}
