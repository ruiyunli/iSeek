package com.izzz.iseek.receiver;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.iseek.R;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.base.BaseMapMain;
import com.izzz.iseek.dialog.LogDialog;
import com.izzz.iseek.setting.SettingActivity;
import com.izzz.iseek.vars.StaticVar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;



/* 自定义继承自BroadcastReceiver类,监听系统服务广播的信息 */
public class SMSreceiver extends BroadcastReceiver 
{ 
	
	@Override 
	public void onReceive(Context context, Intent intent) 
	{ 
		// TODO Auto-generated method stub 	
		
		//系统接收到短信，解析
		if (intent.getAction().equals(StaticVar.SYSTEM_SMS_ACTION)) 
		{ 
			ReceiveMsgCase(context, intent);
		}	
		//接收到refresh发送状态广播
		else if (intent.getAction().equals(StaticVar.COM_SMS_SEND_REFRESH))
		{
			DialogRefresh(BaseMapMain.baseDialog, R.string.DialogSendOK, StaticVar.COM_SMS_SEND_REFRESH);
		}
		//接收到refresh发送回执广播
		else if (intent.getAction().equals(StaticVar.COM_SMS_DELIVERY_REFRESH))
		{
			DialogRefresh(BaseMapMain.baseDialog, R.string.DialogDeliveryOK, StaticVar.COM_SMS_DELIVERY_REFRESH);
		}
		
		//接收到sos设置发送状态广播
		else if(intent.getAction().equals(StaticVar.COM_SMS_SEND_SOS_GPS))
		{
			DialogRefresh(SettingActivity.settingDialog, R.string.DialogSosSendGpsOK, StaticVar.COM_SMS_SEND_SOS_GPS);
			
		}
		//接收到sos设置发送回执广播
		else if(intent.getAction().equals(StaticVar.COM_SMS_DELIVERY_SOS_GPS))
		{
			DialogRefresh(SettingActivity.settingDialog, R.string.DialogSosDeliveryGpsOK, StaticVar.COM_SMS_DELIVERY_SOS_GPS);
		}
		//接收到sos手机号的通知发送状态广播
		else if(intent.getAction().equals(StaticVar.COM_SMS_SEND_SOS_TAR))
		{
			DialogRefresh(SettingActivity.settingDialog, R.string.DialogSosSendTarOK, StaticVar.COM_SMS_DELIVERY_SOS_TAR);
		}
		//接收到sos手机号的通知回执广播
		else if(intent.getAction().equals(StaticVar.COM_SMS_DELIVERY_SOS_TAR))
		{
			DialogRefresh(SettingActivity.settingDialog, R.string.DialogSosDeliveryTarOK, StaticVar.COM_SMS_DELIVERY_SOS_TAR);
		}
		//接收到refresh闹钟广播
		else if(intent.getAction().equals(StaticVar.COM_ALARM_REFRESH))
		{
			DialogRefresh(BaseMapMain.baseDialog, R.string.DialogAlarmGot, StaticVar.COM_ALARM_REFRESH);
		}
		//接收到sos set闹钟广播
		else if(intent.getAction().equals(StaticVar.COM_ALARM_SOS_SET))
		{
			DialogRefresh(SettingActivity.settingDialog, R.string.DialogAlarmGot, StaticVar.COM_ALARM_SOS_SET);
		}
	}
		
	/**
	 * 更新log输出页面的通用接口--message处理
	 * @param logDialog
	 * @param strAppendId
	 * @param strCase
	 */
	private void DialogRefresh(LogDialog logDialog, int strAppendId, String strCase)
	{
		if( getResultCode() == Activity.RESULT_OK || 
			strCase == StaticVar.COM_ALARM_REFRESH)
		{
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "receive sucess in " + strCase);
			logDialog.DialogUpdate(strAppendId);
		}
		else
		{
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "error in receive:" + strCase);
		}
	}	
	
	
	//对系统接收短信进行过滤和解析
	private void ReceiveMsgCase(Context context, Intent intent)
	{
		String mesNumber;
		String mesContext;
		
		//接收由Intent传来的数据
		Bundle bundle = intent.getExtras(); 
		
		//判断Intent是有资料
		if (bundle != null) 
		{ 
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "bundle is not null!");
			
			//pdus为 android内建短信参数 identifier
			//透过bundle.get("")并传一个包含pdus的对象
			Object[] myOBJpdus = (Object[]) bundle.get("pdus");
			
			//建构短信对象array,并依据收到的对象长度来建立array的大小
			SmsMessage[] messages = new SmsMessage[myOBJpdus.length];	          
          	messages[0] = SmsMessage.createFromPdu ((byte[]) myOBJpdus[0]); 
			
			mesNumber = new String(messages[0].getDisplayOriginatingAddress()); 			  

			IseekApplication.getInstance();
			IseekApplication.getInstance();
			String targetPhone = IseekApplication.prefs.getString(
					IseekApplication.prefTargetPhoneKey,"unset");	
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "mesNumber:" + mesNumber + " targetPhone:" + targetPhone);
			
			//有的手机获取号码带有"+86"，但是有的不带有，判断是否属于这两种情况
			if(mesNumber.equals(targetPhone) || mesNumber.equals("+86" + targetPhone))
			{
			
				mesContext = new String(messages[0].getDisplayMessageBody());
				if(StaticVar.DEBUG_ENABLE)
				{
					StaticVar.logPrint('D', "mesNumber:" + mesNumber);
					StaticVar.logPrint('D', "mesContext:" + mesContext);
					StaticVar.logPrint('D', "SMS header:" + mesContext.substring(0, 7));
				}
				//短信头--定位成功短信
				if(mesContext.substring(0, 7).equals(StaticVar.SMS_Header_LOC_SUCCESS))
				{					
					ReceiveMsgCaseLocOK(mesContext);
				}
				//短信头--设置sos号码的gps回复短息
				else if(mesContext.substring(0, 7).equals(StaticVar.SMS_Header_SET_SOS_OK))
				{
					ReceiveMsgCaseSetSosOK(context, mesContext);
				}
				else
				{
					//短信头不匹配--为了调试方便，后期将要删掉
					Toast.makeText(context, "SMS-header Error", Toast.LENGTH_LONG).show();
				}
				
				//不再广播消息，取消保存
				//abortBroadcast();
			}
			else
			{
				//手机号码不匹配
				mesContext = new String(messages[0].getDisplayMessageBody());
				if(StaticVar.DEBUG_ENABLE)
				{
					StaticVar.logPrint('D', "SMSreceiver:different targetPhone");					
					StaticVar.logPrint('D', "diff-mesNumber:" + mesNumber);
					StaticVar.logPrint('D', "diff-mesContext:" + mesContext);
				}
				return ;
			}
		}
		else
		{
			//bundle中为空
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "bundle is null");
		}   
	}
	
	//定位成功
	private void ReceiveMsgCaseLocOK(String msgContext)
	{
		//解析经纬度
		
		//For example:
		//W00,051,34.234442N,108.913805E,1.574Km/h,13-03-21,16:04:43
		int indexTmp = msgContext.indexOf("N");
		String Latitude = msgContext.substring(8, 16);//原来是8-17
		
		//从N后面开始获取，即为经度
		String Longitude = msgContext.substring(indexTmp+2, indexTmp+10);//原来是19-29
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "OnReceive--Latitude:" + Latitude + " Longitude:" + Longitude);
		
		//调用MainActivity中的静态函数，设置地图
		if(isValidGeo(Longitude) && isValidGeo(Latitude))
		{
			int LatitudeInt;
			int LongitudeInt;
			
			LatitudeInt = (int)(Double.parseDouble(Latitude) * 1E6);
			LongitudeInt = (int)(Double.parseDouble(Longitude) * 1E6);
			
			IseekApplication.prefsEditor.putString(IseekApplication.prefOriginLatitudeKey, Integer.toString(LatitudeInt)).commit();
			IseekApplication.prefsEditor.putString(IseekApplication.prefOriginLongitudeKey, Integer.toString(LongitudeInt)).commit();
			
			//符合要求，则取消闹钟关闭logDialog
			IseekApplication.alarmManager.cancel(IseekApplication.alarmPI);
			
			BaseMapMain.baseDialog.dismissLog();
			BaseMapMain.baseDialog.disable();
			
			//WGS84坐标转换为百度坐标
//			CoordinateConver.fromGcjToBaidu   --  GCJ-20(中文谷歌地图)到百度坐标系 
//			CoordinateConver.fromWgs84ToBaidu --  WGS81到百度坐标系转换
			GeoPoint tmpPoint = new GeoPoint(LatitudeInt, LongitudeInt);
			
			BaseMapMain.setNewPosition(tmpPoint);
		}
	}
	
	//设置sos号码成功
	private void ReceiveMsgCaseSetSosOK(Context context, String msgContext)
	{
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', msgContext.substring(8));
		if(msgContext.substring(8).equals(StaticVar.SMS_BODY_SET_SOS_OK))
		{
			DialogRefresh(SettingActivity.settingDialog, R.string.DialogSosFeedBackGpsOK, StaticVar.SMS_BODY_SET_SOS_OK);
		}
	}
	
	//用于经纬度的合法性检测
	public boolean isValidGeo(String geoStr)
	{
		boolean isValid = false;
		
		double tmp;
		tmp = Double.parseDouble(geoStr);
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "isValidGeo--str:" + geoStr + " tmp:" + tmp);
		
		if((tmp>0) && (tmp<140) )
			isValid = true;		
		return isValid;
	}
} 




