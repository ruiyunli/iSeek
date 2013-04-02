package com.izzz.iseek.map;

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

	//ȫ�ֱ���
	
	//��ͼ���
	private static IseekApplication mInstance = null;
    public boolean m_bKeyRight = true;
    BMapManager mBMapManager = null;
    
    //�ؼ���Ӧ��key�ַ�������
  	public static String prefTargetPhoneKey  = null;
  	public static String prefSosNumberKey    = null;
  	public static String prefAboutKey        = null;	
  	public static String prefOriginLatitude  = null;
  	public static String prefOriginLongitude = null;
  	public static SharedPreferences prefs    = null;
  	public static SharedPreferences.Editor prefsEditor = null;	//û���ϣ���������
  	
  	//�������
  	public static AlarmManager alarmManager = null;
	public static PendingIntent alarmPI = null;
    
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
		prefTargetPhoneKey  = getResources().getString(R.string.set_targetPhone_key);
		prefSosNumberKey    = getResources().getString(R.string.set_sosNumber_key);		
		prefAboutKey        = getResources().getString(R.string.set_about_key);
		prefOriginLatitude  = getResources().getString(R.string.set_origin_latitude_key);
		prefOriginLongitude = getResources().getString(R.string.set_origin_longitude_key);
		
		//��ȡprefs��editor
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefsEditor = prefs.edit();
	}
	
	public static IseekApplication getInstance() {
		return mInstance;
	}
	
	// �����¼���������������ͨ�������������Ȩ��֤�����
    static class MyGeneralListener implements MKGeneralListener {
        
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
