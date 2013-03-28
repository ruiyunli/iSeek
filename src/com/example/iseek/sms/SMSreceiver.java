package com.example.iseek.sms;

import com.baidu.mapapi.utils.CoordinateConver;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.iseek.R;
import com.example.iseek.R.string;
import com.example.iseek.vars.StaticVar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.view.inputmethod.CorrectionInfo;
import android.widget.Toast;


/* 自定义继承自BroadcastReceiver类,监听系统服务广播的信息 */
public class SMSreceiver extends BroadcastReceiver 
{ 
	
	@Override 
	public void onReceive(Context context, Intent intent) 
	{ 
		// TODO Auto-generated method stub 
		// 判断传来Intent是否为短信
		
		System.out.println("OnReceive-Action:" + intent.getAction());
		
		//系统接收到短信，解析
		if (intent.getAction().equals(StaticVar.SYSTEM_SMS_ACTION)) 
		{ 
			ReceiveMessageCase(context, intent);
		}		
		
		//接收到发送状态广播		
		else if (intent.getAction().equals(StaticVar.COM_SMS_SEND))
		{
			if(getResultCode()== Activity.RESULT_OK)
			{
			    StaticVar.logMessage = StaticVar.logMessage + "\n" 
			    		+ context.getResources().getText(R.string.DialogSendOK);
			    StaticVar.logDialog.setMessage(StaticVar.logMessage);			    
			}
			else
			{
				StaticVar.logMessage = StaticVar.logMessage + "\n" + 
						context.getResources().getText(R.string.DialogSendOK);
			    StaticVar.logDialog.setMessage(StaticVar.logMessage);
			}
		}
		//接收到发送回执广播
		else if (intent.getAction().equals(StaticVar.COM_SMS_DELIVERY))
		{
			System.out.println("收到发送回执");
			if(getResultCode()== Activity.RESULT_OK)
			{
			    StaticVar.logMessage = StaticVar.logMessage + "\n" + 
			    		context.getResources().getText(R.string.DialogDeliveryOK);
			    StaticVar.logDialog.setMessage(StaticVar.logMessage);
			}
			else
			{
				StaticVar.logMessage = StaticVar.logMessage + "\n" + context.getResources().getText(R.string.DialogDeliveryNO);
			    StaticVar.logDialog.setMessage(StaticVar.logMessage);
			}
		}
	} 
	
	//对系统接收短信进行过滤和解析
	private void ReceiveMessageCase(Context context, Intent intent)
	{
		String mesNumber;
		String mesContext;
		
		//接收由Intent传来的数据
		Bundle bundle = intent.getExtras(); 
		
		//判断Intent是有资料
		if (bundle != null) 
		{ 
		
			System.out.println("bundle is not null!");
			
			//pdus为 android内建短信参数 identifier
			//透过bundle.get("")并传一个包含pdus的对象
			Object[] myOBJpdus = (Object[]) bundle.get("pdus");
			
			//建构短信对象array,并依据收到的对象长度来建立array的大小
			SmsMessage[] messages = new SmsMessage[myOBJpdus.length];	          
          	messages[0] = SmsMessage.createFromPdu ((byte[]) myOBJpdus[0]); 
			
			mesNumber = new String(messages[0].getDisplayOriginatingAddress()); 			  

			String targetPhone = StaticVar.prefs.getString(
					StaticVar.prefTargetPhoneKey,"unset");			
			System.out.println("mesNumber:" + mesNumber + " targetPhone:" + targetPhone);
			
			//有的手机获取号码带有"+86"，但是有的不带有，判断是否属于这两种情况
			if(mesNumber.equals(targetPhone) || mesNumber.equals("+86" + targetPhone))
			{
			
				mesContext = new String(messages[0].getDisplayMessageBody());				
				System.out.println("mesNumber:" + mesNumber);
				System.out.println("mesContext:" + mesContext);
				System.out.println(mesContext.substring(0, 7));
				
				//短信头判断，是否匹配
				if(mesContext.substring(0, 7).equals(StaticVar.SMS_Header_LOC_SUCCESS))
				{
					//解析经纬度
					//For example:
					//W00,051,34.234442N,108.913805E,1.574Km/h,13-03-21,16:04:43
					int indexTmp = mesContext.indexOf("N");
					String Latitude = mesContext.substring(8, 16);//原来是8-17
					
					//从N后面开始获取，即为经度
					String Longitude = mesContext.substring(indexTmp+2, indexTmp+10);//原来是19-29
					System.out.println("OnReceive--Latitude:" + Latitude + " Longitude:" + Longitude);
					
					//调用MainActivity中的静态函数，设置地图
					if(isValidGeo(Longitude) && isValidGeo(Latitude))
					{
						//符合要求，则关闭logDialog
						StaticVar.logDialog.dismiss();
						
						//WGS84坐标转换为百度坐标
						GeoPoint tmpPoint = CoordinateConver.fromGcjToBaidu(
								new GeoPoint((int)(Double.parseDouble(Latitude)* 1E6),(int)(Double.parseDouble(Longitude)* 1E6)));
						
						StaticVar.setNewPosition(tmpPoint.getLatitudeE6()/(1E6),tmpPoint.getLongitudeE6()/(1E6));
					}

				}
				else
				{
					//短信头不匹配--为了调试方便，后期将要删掉
					Toast.makeText(context, "SMS-header Error", Toast.LENGTH_LONG).show();
				}
			}
			else
			{
				//手机号码不匹配
				System.out.println("SMSreceiver:different targetPhone");					
				mesContext = new String(messages[0].getDisplayMessageBody());
				System.out.println("diff-mesNumber:" + mesNumber);
				System.out.println("diff-mesContext:" + mesContext);
				return ;
			}
		}
		else
		{
			//bundle中为空
			System.out.println("bundle is null");
		}   
	}

	
	//用于经纬度的合法性检测
	public boolean isValidGeo(String geoStr)
	{
		boolean isValid = false;
		
		double tmp;
		tmp = Double.parseDouble(geoStr);
		
		System.out.println("isValidGeo--str:" + geoStr + " tmp:" + tmp);
		
		if((tmp>0) && (tmp<140) )
			isValid = true;		
		return isValid;
	}
} 




