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

	private static IseekApplication mInstance = null;	//��ͼ���
	
    public boolean m_bKeyRight = true;					//��ͼ���
    
    public BMapManager mBMapManager = null;				//��ͼ���
    
    public PhoneLocation mPhoneLocation = null;			//��λ���
    
    public PrefHolder prefHolder = null;				//sharedpreference����
    
  	public static int DOWNLOAD_CHANNEL = StaticVar.OFFLINE_NULL;	//��������ҳ�������;����־
  	
  	public static boolean CORRECTION_ENABLE = false;	//����ʹ��У׼����
  	
  	public static boolean CORRECTION_USED 	= false;	//ʹ����У׼����
  	
  	public static boolean GPS_LOCATE_OK = false;		//gps��λ�ɹ�
  	
  	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
//		mPhoneLocation = new PhoneLocation(this);
		
		super.onCreate();
		
		mInstance = this;			//thisָ��
		
		initEngineManager(this);	//�ٶȵ�ͼBMapManger����
		
		InitPrefs();				//��ʼ��shareedPreference
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
        		StaticVar.logPrint('D', "BMapManager ��ʼ������!");
        }
	}
	
	//���ڳ�ʼ��PreferenceActivity�����key
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
	
	// �����¼���������������ͨ�������������Ȩ��֤�����
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
                //��ȨKey����
            	if(StaticVar.DEBUG_ENABLE)
            		Toast.makeText(IseekApplication.getInstance().getApplicationContext(), R.string.ToastErrorMapKey, 
            				Toast.LENGTH_LONG).show();
                IseekApplication.getInstance().m_bKeyRight = false;
            }
        }
    }
}
