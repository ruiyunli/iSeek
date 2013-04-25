package com.izzz.iseek.app;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.example.iseek.R;
import com.izzz.iseek.base.BaseMapMain;
import com.izzz.iseek.map.PhoneLocation;
import com.izzz.iseek.vars.StaticVar;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class IseekApplication extends Application {

	private static IseekApplication mInstance = null;	//地图相关
	
    public boolean m_bKeyRight = true;					//地图相关
    
    public BMapManager mBMapManager = null;				//地图相关
    
    public PhoneLocation mPhoneLocation = null;		//定位相关
    
  	public static String prefTargetPhoneKey  	= null;			//控件对应的key字符串声明
  	
  	public static String prefSosNumberKey    	= null;			//控件对应的key字符串声明
  	
  	public static String prefCorrEntryKey     	= null;			//控件对应的key字符串声明
  	
  	public static String prefCorrEnableKey		= null;			//控件对应的key字符串声明
  	
  	public static String prefOfflineKey			= null;			//控件对应的key字符串声明
  	
  	public static String prefGuideKey			= null;			//控件对应的key字符串声明
  	
  	public static String prefAboutKey        	= null;			//控件对应的key字符串声明
  	
  	public static String prefOriginLatitudeKey  = null;			//控件对应的key字符串声明
  	
  	public static String prefOriginLongitudeKey = null;			//控件对应的key字符串声明
  	
  	public static SharedPreferences prefs    	= null;			//控件对应的key字符串声明
  	
  	public static SharedPreferences.Editor prefsEditor = null;	//控件对应的key字符串声明
  	
  	public static int DOWNLOAD_CHANNEL = StaticVar.OFFLINE_NULL;	//离线下载页面的下载途径标志
  	
  	public static boolean CORRECTION_ENABLE = false;
  	
  	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
		mPhoneLocation = new PhoneLocation(this);
		
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
		prefTargetPhoneKey  	= getResources().getString(R.string.set_targetPhone_key);
		prefSosNumberKey    	= getResources().getString(R.string.set_sosNumber_key);
		prefCorrEntryKey   		= getResources().getString(R.string.set_correntry_key);
		prefOfflineKey			= getResources().getString(R.string.set_offline_key);
		prefGuideKey			= getResources().getString(R.string.set_guide_key);
		prefCorrEnableKey 		= getResources().getString(R.string.set_correnable_key);
		prefAboutKey        	= getResources().getString(R.string.set_about_key);
		prefOriginLatitudeKey  	= getResources().getString(R.string.set_origin_latitude_key);
		prefOriginLongitudeKey 	= getResources().getString(R.string.set_origin_longitude_key);
		
		//获取prefs和editor
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefsEditor = prefs.edit();
		
		CORRECTION_ENABLE = prefs.getBoolean(prefCorrEnableKey,false); 
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "correction enable:" + CORRECTION_ENABLE);
	}
	
	public static IseekApplication getInstance() {
		return mInstance;
	}
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    public static class MyGeneralListener implements MKGeneralListener {
        
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
