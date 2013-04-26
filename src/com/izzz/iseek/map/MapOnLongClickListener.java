package com.izzz.iseek.map;

import com.example.iseek.R;
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
		
		new AlertDialog.Builder(mContext) 
	    .setTitle(R.string.CorrAlertTitle)
	    .setMessage(R.string.CorrAlertMsg)
	    .setPositiveButton(R.string.OK_CH, new AlertCorrOnClickListener())
	    .setNegativeButton(R.string.CANCLE_CH, null)
	    .show();
		
		return false;
	}
	
	class AlertCorrOnClickListener implements DialogInterface.OnClickListener
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
//			BaseMapMain.correction.CORRECTION_START = true;
//			BaseMapMain.correction.SetAllButtonVisible();
			
			BaseMapMain.correction.EnterCorrection();
			
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "Correction started!");
		}
		
	}

}
