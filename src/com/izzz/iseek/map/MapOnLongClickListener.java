package com.izzz.iseek.map;

import com.izzz.iseek.R;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.base.BaseMapMain;
import com.izzz.iseek.vars.StaticVar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnLongClickListener;

public class MapOnLongClickListener implements OnLongClickListener{

	private Context mContext = null;
	
	public MapOnLongClickListener(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "longClick");
		
		//确保在定位成功后再校准,并且定位过程中没有使用自动校准
		if(IseekApplication.GPS_LOCATE_OK && !IseekApplication.CORRECTION_USED)
			new AlertDialog.Builder(mContext) 
			    .setTitle(R.string.CorrAlertTitle)
			    .setMessage(R.string.CorrAlertMsgWarn)
			    .setPositiveButton(R.string.OK_CH, new AlertCorrOnClickListener())
			    .setNegativeButton(R.string.CANCLE_CH, null)
			    .show();
		//没有定位成功
		else if(!IseekApplication.GPS_LOCATE_OK)
		{
			new AlertDialog.Builder(mContext) 
			    .setTitle(R.string.CorrAlertTitle)
			    .setMessage(R.string.CorrAlertMsgNoLocate)
			    .setPositiveButton(R.string.OK_CH, null)
			    .show();
		}
		//使用了自动校准
		else if(IseekApplication.CORRECTION_USED)
		{
			new AlertDialog.Builder(mContext) 
			    .setTitle(R.string.CorrAlertTitle)
			    .setMessage(R.string.CorrAlertMsgCorrUsed)
			    .setPositiveButton(R.string.OK_CH, null)
			    .show();
		}
		
		return true;
	}
	
	class AlertCorrOnClickListener implements DialogInterface.OnClickListener
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			
			BaseMapMain.corrView.EnterCorrection();
			
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "Correction started!");
		}
		
	}

}
