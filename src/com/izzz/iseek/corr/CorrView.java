package com.izzz.iseek.corr;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.izzz.iseek.R;
import com.izzz.iseek.base.BaseMapMain;
import com.izzz.iseek.map.CorrectionOverlay;
import com.izzz.iseek.tools.PrefHolder;
import com.izzz.iseek.vars.StaticVar;

public class CorrView {
	
	private Context mContext = null;
	
	public MapView mMapView = null;

	public CorrectionOverlay correctionOverlay = null;	//校准用变量
	
	public GeoPoint corrPoint = null;					//校准用变量
	
	public OverlayItem corrItem = null;					//校准用变量	
	
	private ImageButton btnMoveUp    = null;				//微调button
	
	private ImageButton btnMoveDown  = null;				//微调button
	
	private ImageButton btnMoveLeft  = null;				//微调button
	
	private ImageButton btnMoveRight = null;				//微调button
	
	private Button btnCorrOk = null;						//校准的两个按钮
	
	private Button btnCorrCancle = null;					//校准的两个按钮
	
	public boolean ADD_LAYER_FLAG = false;				//是否添加有覆盖层
	
	public boolean CORRECTION_START = false;			//主要用于在ontouch响应中决定是否显示红叉
	
	private CorrPointManager corrPM = null;

	public CorrView(Context mContext, MapView mMapView, ImageButton btnMoveUp,
			ImageButton btnMoveDown, ImageButton btnMoveLeft,
			ImageButton btnMoveRight, Button btnCorrOk, Button btnCorrCancle) {
		super();
		this.mContext = mContext;
		this.mMapView = mMapView;
		this.btnMoveUp = btnMoveUp;
		this.btnMoveDown = btnMoveDown;
		this.btnMoveLeft = btnMoveLeft;
		this.btnMoveRight = btnMoveRight;
		this.btnCorrOk = btnCorrOk;
		this.btnCorrCancle = btnCorrCancle;
		
		corrPM = new CorrPointManager(mContext, PrefHolder.prefs, PrefHolder.prefsEditor);
		
		btnCorrOk.setOnClickListener(new CorrOnClickListener());
		btnCorrCancle.setOnClickListener(new CorrOnClickListener());
		btnMoveUp.setOnClickListener(new CorrOnClickListener());
		btnMoveDown.setOnClickListener(new CorrOnClickListener());
		btnMoveLeft.setOnClickListener(new CorrOnClickListener());
		btnMoveRight.setOnClickListener(new CorrOnClickListener());
		
		correctionOverlay = new CorrectionOverlay(mContext.getResources().getDrawable(R.drawable.icon_mapselect));
	}
	
	/**校准所有按钮不显示*/
	public void SetAllButtonGone()
	{
		SetMoveButtonGone();
		SetOkButtonGone();
	}
	
	/**校准微调按钮不显示*/
	public void SetMoveButtonGone()
	{
		btnMoveUp.setVisibility(View.GONE);
		btnMoveLeft.setVisibility(View.GONE);
		btnMoveRight.setVisibility(View.GONE);
		btnMoveDown.setVisibility(View.GONE);
	}
	
	/**校准ok和canle按钮不显示*/
	public void SetOkButtonGone()
	{
		btnCorrCancle.setVisibility(View.GONE);
		btnCorrOk.setVisibility(View.GONE);
	}
	
	/**校准所有按钮显示*/
	public void SetAllButtonVisible()
	{
		SetMoveButtonVisible();
		SetOkButtonVisible();
	}
	
	/**校准微调按钮显示*/
	public void SetMoveButtonVisible()
	{
		btnMoveUp.setVisibility(View.VISIBLE);
		btnMoveLeft.setVisibility(View.VISIBLE);
		btnMoveRight.setVisibility(View.VISIBLE);
		btnMoveDown.setVisibility(View.VISIBLE);
	}
	
	/**校准ok和canle按钮显示*/
	public void SetOkButtonVisible()
	{
		btnCorrCancle.setVisibility(View.VISIBLE);
		btnCorrOk.setVisibility(View.VISIBLE);
	}
	
	/**去掉红叉显示层*/
	public void RemoveOverlay()
	{
		if(ADD_LAYER_FLAG)
			mMapView.getOverlays().remove(correctionOverlay);
	}
	
	/**添加红叉显示层*/
	public void AddOverlay()
	{
		if(!ADD_LAYER_FLAG)
			mMapView.getOverlays().add(correctionOverlay);
	}
	
	/** 进入校准 */
	public void EnterCorrection()
	{
		if(corrPM.isCorrEnable())
		{
			CORRECTION_START = true;
			SetAllButtonVisible();
		}
		else
			Toast.makeText(mContext, R.string.ToastCorrPointFull, Toast.LENGTH_LONG).show();
	}
	
	/** 退出校准 */
	public void ExitCorrection()
	{
		SetAllButtonGone();
		//去掉校准层
		RemoveOverlay();
		mMapView.refresh();
		
		ADD_LAYER_FLAG = false;
		CORRECTION_START = false;
		
	}
	
	/**校准顺利结束*/
	public void CorrectionOK(View v)
	{
		if(isCorrPointNull())
		{
			Toast.makeText(v.getContext(), R.string.ToastCorrPressFirst, Toast.LENGTH_LONG).show();
			return ;
		}
		
		try {
			corrPM.add(BaseMapMain.gpsPoint.getLongitudeE6(), corrPoint.getLongitudeE6());
		} catch (Exception e) {
			// TODO: handle exception
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', e.toString());
		}
		
		ExitCorrection();
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "correction ok!");
	}
	
	public boolean isCorrPointNull()
	{
		if(corrPoint == null)
			return true;
		else
			return false;
	}
	

	/**按钮响应函数*/
	public class CorrOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			if ( v.getId() == btnMoveUp.getId() )
				MoveCorration(v, StaticVar.MOVE_CORR_UP);
			else if ( v.getId() == btnMoveDown.getId() )
				MoveCorration(v, StaticVar.MOVE_CORR_DOWN);
			else if ( v.getId() == btnMoveLeft.getId() )
				MoveCorration(v, StaticVar.MOVE_CORR_LEFT);
			else if ( v.getId() == btnMoveRight.getId() )
				MoveCorration(v, StaticVar.MOVE_CORR_RIGHT);			
			else if(v.getId() == btnCorrCancle.getId())
				ExitCorrection();
			else if (v.getId() == btnCorrOk.getId())
				CorrectionOK(v);
			
		}
		
		
		
		//微调共用函数
		private void MoveCorration(View v, int direction)
		{
			if(isCorrPointNull())
			{
				Toast.makeText(v.getContext(), R.string.ToastCorrPressFirst, Toast.LENGTH_LONG).show();
				return ;
			}
			
			switch(direction)
			{
			case StaticVar.MOVE_CORR_UP:
				corrPoint.setLatitudeE6(corrPoint.getLatitudeE6() + StaticVar.CORR_STEP);
				break;
			case StaticVar.MOVE_CORR_DOWN:
				corrPoint.setLatitudeE6(corrPoint.getLatitudeE6() - StaticVar.CORR_STEP);
				break;
			case StaticVar.MOVE_CORR_LEFT:
				corrPoint.setLongitudeE6(corrPoint.getLongitudeE6() - StaticVar.CORR_STEP);
				break;
			case StaticVar.MOVE_CORR_RIGHT:
				corrPoint.setLongitudeE6(corrPoint.getLongitudeE6() + StaticVar.CORR_STEP);
				break;
			}
			corrItem =new OverlayItem(corrPoint, "title", "snippet");
			correctionOverlay.refreshItem(corrItem);
			
			mMapView.refresh();
			
			if(StaticVar.DEBUG_ENABLE)
			{
				StaticVar.logPrint('D', "x: unknown" + " y: unknown" 
		                + "\n" + " latitude: " + corrPoint.getLatitudeE6()
		                +" longitude: " + corrPoint.getLongitudeE6());
				BaseMapMain.logText.setText("x: unknown" + " y: unknown" 
		                + "\n" + " latitude: " + corrPoint.getLatitudeE6()
		                +" longitude: " + corrPoint.getLongitudeE6());
			}
		}
		
		
	}
}
