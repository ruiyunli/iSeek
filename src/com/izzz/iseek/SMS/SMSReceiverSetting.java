package com.izzz.iseek.SMS;

import com.izzz.iseek.R;
import com.izzz.iseek.activity.SettingActivity;
import com.izzz.iseek.vars.PrefHolder;
import com.izzz.iseek.vars.StaticVar;
import com.izzz.iseek.view.LogDialog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;



/* 自定义继承自BroadcastReceiver类,监听系统服务广播的信息 */
public class SMSReceiverSetting extends BroadcastReceiver 
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
			strCase == StaticVar.COM_ALARM_REFRESH ||
			strCase == StaticVar.COM_ALARM_SOS_SET )
		{
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "receive sucess in " + strCase);
			logDialog.DialogUpdate(strAppendId);
		}
		else
		{
			
			if(StaticVar.DEBUG_ENABLE)
			{
				StaticVar.logPrint('D', "error in receive:" + strCase);
				StaticVar.logPrint('D', "result:" + Activity.RESULT_OK);
			}
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
			
			String targetPhone = PrefHolder.prefs.getString(
					PrefHolder.prefTargetPhoneKey,"unset");	
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
				//短信头--设置sos号码的gps回复短息
				if(mesContext.substring(0, 3).equals("W01"))
				{
					if(StaticVar.DEBUG_ENABLE)
						StaticVar.logPrint('D', "set sos ok case!");
					ReceiveMsgCaseSetSosOK(context, mesContext , SettingActivity.settingDialog );
				}
				else
				{
					//短信头不匹配--为了调试方便，后期将要删掉
					if(StaticVar.DEBUG_ENABLE)
					{
						StaticVar.logPrint('D', "set sos error case!");
					
						Toast.makeText(context, "setting:unknown sms", Toast.LENGTH_SHORT).show();
					}
				}
				
				//不再广播消息，取消保存
				if(!StaticVar.DEBUG_ENABLE)
					abortBroadcast();
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
	
	//设置sos号码成功
	private void ReceiveMsgCaseSetSosOK(Context context, String msgContext, LogDialog logDialog)
	{
		
		if(msgContext.substring(8).equals(StaticVar.SMS_BODY_SET_SOS_OK))
		{
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "sos number set sucess!");
			
//			SettingActivity.alarmHandler.Stop();
			DialogRefresh(SettingActivity.settingDialog, R.string.DialogSosFeedBackGpsOK, StaticVar.SMS_BODY_SET_SOS_OK);
			SettingActivity.settingDialog.disable();
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




