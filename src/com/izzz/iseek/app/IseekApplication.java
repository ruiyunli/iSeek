package com.izzz.iseek.app;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.izzz.iseek.R;
import com.izzz.iseek.maplocate.PhoneLocation;
import com.izzz.iseek.vars.PrefHolder;
import com.izzz.iseek.vars.StaticVar;

public class IseekApplication extends Application {

	private static IseekApplication mInstance = null;	//地图相关
	
    public boolean m_bKeyRight = true;					//地图相关
    
    public BMapManager mBMapManager = null;				//地图相关
    
    public PhoneLocation mPhoneLocation = null;			//定位相关
    
    public PrefHolder prefHolder = null;				//sharedpreference管理
    
  	public static int DOWNLOAD_CHANNEL = StaticVar.OFFLINE_NULL;	//离线下载页面的下载途径标志
  	
  	public static boolean CORRECTION_ENABLE = false;	//允许使用校准功能
  	
  	public static boolean CORRECTION_USED 	= false;	//使用了校准功能
  	
  	public static boolean GPS_LOCATE_OK = false;		//gps定位成功
  	
  	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
//		mPhoneLocation = new PhoneLocation(this);
		
		super.onCreate();
		
		mInstance = this;			//this指针
		
		initEngineManager(this);	//百度地图BMapManger对象
		
		InitPrefs();				//初始化shareedPreference
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
        	if(StaticVar.DEBUG_ENABLE)
        		StaticVar.logPrint('D', "BMapManager 初始化错误!");
        }
	}
	
	//用于初始化PreferenceActivity的相关key
	private void InitPrefs()
	{
		prefHolder = new PrefHolder(this);
		
		CORRECTION_ENABLE = PrefHolder.prefs.getBoolean(PrefHolder.prefCorrEnableKey,false); 
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
            	Toast.makeText(IseekApplication.getInstance().getApplicationContext(), R.string.ToastErrorInternet,
            				Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
            	Toast.makeText(IseekApplication.getInstance().getApplicationContext(), R.string.ToastErrorSearchData,
            				Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //授权Key错误：
            	if(StaticVar.DEBUG_ENABLE)
            		Toast.makeText(IseekApplication.getInstance().getApplicationContext(), R.string.ToastErrorMapKey, 
            				Toast.LENGTH_LONG).show();
                IseekApplication.getInstance().m_bKeyRight = false;
            }
        }
    }
}
