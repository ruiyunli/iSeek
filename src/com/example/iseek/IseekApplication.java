package com.example.iseek;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.example.iseek.vars.StaticVar;


import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class IseekApplication extends Application {

	private static IseekApplication mInstance = null;
    public boolean m_bKeyRight = true;
    BMapManager mBMapManager = null;
    
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mInstance = this;
		initEngineManager(this);
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
