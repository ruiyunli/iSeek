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

	//全局变量
	
	//地图相关
	private static IseekApplication mInstance = null;
    public boolean m_bKeyRight = true;
    BMapManager mBMapManager = null;
    
    //控件对应的key字符串声明
  	public static String prefTargetPhoneKey  = null;
  	public static String prefSosNumberKey    = null;
  	public static String prefAboutKey        = null;	
  	public static String prefOriginLatitude  = null;
  	public static String prefOriginLongitude = null;
  	public static SharedPreferences prefs    = null;
  	public static SharedPreferences.Editor prefsEditor = null;	//没用上！！！！！
  	
  	//闹铃相关
  	public static AlarmManager alarmManager = null;
	public static PendingIntent alarmPI = null;
    
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		//this指针
		mInstance = this;
		
		//百度地图BMapManger对象
		initEngineManager(this);
		
		//初始化shareedPreference
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
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
	}
	
	//用于初始化PreferenceActivity的相关key
	private void InitPrefs()
	{
		//获取控件key字符串
		prefTargetPhoneKey  = getResources().getString(R.string.set_targetPhone_key);
		prefSosNumberKey    = getResources().getString(R.string.set_sosNumber_key);		
		prefAboutKey        = getResources().getString(R.string.set_about_key);
		prefOriginLatitude  = getResources().getString(R.string.set_origin_latitude_key);
		prefOriginLongitude = getResources().getString(R.string.set_origin_longitude_key);
		
		//获取prefs和editor
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefsEditor = prefs.edit();
	}
	
	public static IseekApplication getInstance() {
		return mInstance;
	}
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(IseekApplication.getInstance().getApplicationContext(), "您的网络出错啦！",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(IseekApplication.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //授权Key错误：
                Toast.makeText(IseekApplication.getInstance().getApplicationContext(), 
                        "请在 DemoApplication.java文件输入正确的授权Key！", Toast.LENGTH_LONG).show();
                IseekApplication.getInstance().m_bKeyRight = false;
            }
        }
    }
	
	
	
	

}
