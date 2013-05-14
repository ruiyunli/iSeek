package com.izzz.iseek.correction;

import com.izzz.iseek.R;
import com.izzz.iseek.vars.PrefHolder;
import com.izzz.iseek.vars.StaticVar;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class CorrPointManager {

	private Context mContext = null;
	
	private SharedPreferences prefs = null;
	
	private SharedPreferences.Editor prefsEditor = null;
	
	public static final int  MAX_POINT_SIZE = 10;
	
	public String prefCorrPointSizeKey 	= null;			//控件对应的key字符串声明
  	
  	public String[] prefOriginPointKey 	= null;			//控件对应的key字符串声明
  	
  	public String[] prefTargetPointKey 	= null;			//控件对应的key字符串声明
  	
  	public static String prefCorrCoefAKey		= null;
  	
  	public static String prefCorrCoefBKey		= null;

	public CorrPointManager(Context mContext, SharedPreferences prefs,	Editor prefsEditor) 
	{
		super();
		this.mContext = mContext;
		this.prefs = prefs;
		this.prefsEditor = prefsEditor;
		
		//校准内容存储
		prefOriginPointKey 			= new String[MAX_POINT_SIZE];
		prefTargetPointKey 			= new String[MAX_POINT_SIZE];
		
		prefOriginPointKey[0]		= mContext.getResources().getString(R.string.origin_point_0_key);
		prefOriginPointKey[1]		= mContext.getResources().getString(R.string.origin_point_1_key);
		prefOriginPointKey[2]		= mContext.getResources().getString(R.string.origin_point_2_key);
		prefOriginPointKey[3]		= mContext.getResources().getString(R.string.origin_point_3_key);
		prefOriginPointKey[4]		= mContext.getResources().getString(R.string.origin_point_4_key);
		prefOriginPointKey[5]		= mContext.getResources().getString(R.string.origin_point_5_key);
		prefOriginPointKey[6]		= mContext.getResources().getString(R.string.origin_point_6_key);
		prefOriginPointKey[7]		= mContext.getResources().getString(R.string.origin_point_7_key);
		prefOriginPointKey[8]		= mContext.getResources().getString(R.string.origin_point_8_key);
		prefOriginPointKey[9]		= mContext.getResources().getString(R.string.origin_point_9_key);
		
		prefTargetPointKey[0]		= mContext.getResources().getString(R.string.target_point_0_key);
		prefTargetPointKey[1]		= mContext.getResources().getString(R.string.target_point_1_key);
		prefTargetPointKey[2]		= mContext.getResources().getString(R.string.target_point_2_key);
		prefTargetPointKey[3]		= mContext.getResources().getString(R.string.target_point_3_key);
		prefTargetPointKey[4]		= mContext.getResources().getString(R.string.target_point_4_key);
		prefTargetPointKey[5]		= mContext.getResources().getString(R.string.target_point_5_key);
		prefTargetPointKey[6]		= mContext.getResources().getString(R.string.target_point_6_key);
		prefTargetPointKey[7]		= mContext.getResources().getString(R.string.target_point_7_key);
		prefTargetPointKey[8]		= mContext.getResources().getString(R.string.target_point_8_key);
		prefTargetPointKey[9]		= mContext.getResources().getString(R.string.target_point_9_key);
		
		prefCorrPointSizeKey	= mContext.getResources().getString(R.string.corr_point_size_key);
		
		prefCorrCoefAKey		= mContext.getResources().getString(R.string.corr_coef_a_key);
		prefCorrCoefBKey		= mContext.getResources().getString(R.string.corr_coef_b_key);
	}


	/**获取存储数据量大小*/
	public int getSize()
	{
		return prefs.getInt(prefCorrPointSizeKey, 0);
	}
	
	/**是否可以校准*/
	public boolean isCorrEnable()
	{
		int pointSize = getSize();

		if(pointSize == MAX_POINT_SIZE)
		{
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "point full");
			return false;
		}
		else if(pointSize > MAX_POINT_SIZE)
		{
			if(StaticVar.DEBUG_ENABLE)
			{
				StaticVar.logPrint('D', "point overflow");
				Toast.makeText(mContext, "point overflow", Toast.LENGTH_SHORT).show();
			}
			return false;
		}		
		return true;
	}
	
	/**读取原始数据*/
	public int[] getOriginData()
	{
		int pointSize = getSize();
		int[] originPoint = new int[pointSize];
		
		for(int i =0; i<pointSize ;++i)
		{
			originPoint[i] = prefs.getInt(prefOriginPointKey[i], 0);
			
			if(originPoint[i] == 0)
				if(StaticVar.DEBUG_ENABLE)
					StaticVar.logPrint('D', "get origin point " + i + " longitude is 0!");
		}
		
		return originPoint;
	}
	
	/**读取校准数据*/
	public int[] getTargetData()
	{
		int pointSize = getSize();
		int[] corrPoint = new int[pointSize];
		
		for(int i =0; i<pointSize ;++i)
		{
			corrPoint[i] = prefs.getInt(prefTargetPointKey[i], 0);
			
			if(corrPoint[i] == 0)
				if(StaticVar.DEBUG_ENABLE)
					StaticVar.logPrint('D', "get corr point " + i + " longitude is 0!");
		}
		
		return corrPoint;
	}
	
	/**获取第position个原始数据*/
	public double getOriginData(int position)
	{
		double originData = 0.0;
		int tmp = 0;
		
		tmp = prefs.getInt(prefOriginPointKey[position], 0);
		
		originData = tmp/1E6;
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "int:" + tmp + "originData:" + originData);
		
		return originData;
	}
	
	/**获取第position个目标数据*/
	public double getTargetData(int position)
	{
		double targetData = 0.0;
		int tmp = 0;
		
		tmp = prefs.getInt(prefTargetPointKey[position], 0);
		
		targetData = tmp/1E6;
		
		return targetData;
	}
	
	/**添加坐标组*/
	public void add(int OriginValue, int CorrValue)
	{
		if(isCorrEnable())
		{
			int pointSize = getSize();
			prefsEditor.putInt(prefOriginPointKey[pointSize], OriginValue);
			prefsEditor.putInt(prefTargetPointKey[pointSize], CorrValue);
			prefsEditor.putInt(prefCorrPointSizeKey, pointSize+1).commit();
		}
	}
	
	/**删除数据组*/
	public void remove(int position)
	{
		int pointSize = getSize();
		
		//越界
		if(position >pointSize)
		{
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "delete corr point error");
		}
		//最后一组数据
		else if(position == pointSize)
		{
			prefsEditor.remove(prefOriginPointKey[position-1]);				//减一原因：在ArrayList中第0项为目录
			prefsEditor.remove(prefTargetPointKey[position-1]);
			prefsEditor.putInt(prefCorrPointSizeKey, getSize()-1).commit();
			
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "delete corr point " + pointSize + "ok");
		}
		//不是最后一组数据
		else
		{
			int tmp = 0;
			for(int i = position ; i<pointSize; i++)
			{
				//处理origin
				tmp = prefs.getInt(prefOriginPointKey[i], 0);
				if(tmp == 0)
					if(StaticVar.DEBUG_ENABLE)
						StaticVar.logPrint('D', "delete corr point:get zero!");
				prefsEditor.putInt(prefOriginPointKey[i-1], tmp);
				//处理target
				tmp = prefs.getInt(prefTargetPointKey[i], 0);
				if(tmp == 0)
					if(StaticVar.DEBUG_ENABLE)
						StaticVar.logPrint('D', "delete corr point:get zero!");
				prefsEditor.putInt(prefTargetPointKey[i-1], tmp).commit();
			}
			//删除末尾
			prefsEditor.remove(prefOriginPointKey[pointSize-1]);
			prefsEditor.remove(prefTargetPointKey[pointSize-1]);
			prefsEditor.putInt(prefCorrPointSizeKey, getSize()-1).commit();
			
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "delete corr point ok");
		}
		
	}
	
	/**保存转换系数*/
	public void saveCoef(double coefA, double coefB)
	{
		prefsEditor.putFloat(prefCorrCoefAKey, (float) coefA);
		prefsEditor.putFloat(prefCorrCoefBKey, (float) coefB).commit();
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "save coef A and B ok!");
	}
	
	
	/**获取转换系数*/
	
	
	public static double[] getCoef()
	{
		double[] AB = new double[]{0,0};
		
		AB[0] = (double)PrefHolder.prefs.getFloat(prefCorrCoefAKey, 1);
		AB[1] = (double)PrefHolder.prefs.getFloat(prefCorrCoefBKey, 0);
		
		return AB;
	}
	
}
