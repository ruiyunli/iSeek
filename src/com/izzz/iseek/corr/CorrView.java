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

	public CorrectionOverlay correctionOverlay = null;	//У׼�ñ���
	
	public GeoPoint corrPoint = null;					//У׼�ñ���
	
	public OverlayItem corrItem = null;					//У׼�ñ���	
	
	private ImageButton btnMoveUp    = null;				//΢��button
	
	private ImageButton btnMoveDown  = null;				//΢��button
	
	private ImageButton btnMoveLeft  = null;				//΢��button
	
	private ImageButton btnMoveRight = null;				//΢��button
	
	private Button btnCorrOk = null;						//У׼��������ť
	
	private Button btnCorrCancle = null;					//У׼��������ť
	
	public boolean ADD_LAYER_FLAG = false;				//�Ƿ�����и��ǲ�
	
	public boolean CORRECTION_START = false;			//��Ҫ������ontouch��Ӧ�о����Ƿ���ʾ���
	
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
	
	/**У׼���а�ť����ʾ*/
	public void SetAllButtonGone()
	{
		SetMoveButtonGone();
		SetOkButtonGone();
	}
	
	/**У׼΢����ť����ʾ*/
	public void SetMoveButtonGone()
	{
		btnMoveUp.setVisibility(View.GONE);
		btnMoveLeft.setVisibility(View.GONE);
		btnMoveRight.setVisibility(View.GONE);
		btnMoveDown.setVisibility(View.GONE);
	}
	
	/**У׼ok��canle��ť����ʾ*/
	public void SetOkButtonGone()
	{
		btnCorrCancle.setVisibility(View.GONE);
		btnCorrOk.setVisibility(View.GONE);
	}
	
	/**У׼���а�ť��ʾ*/
	public void SetAllButtonVisible()
	{
		SetMoveButtonVisible();
		SetOkButtonVisible();
	}
	
	/**У׼΢����ť��ʾ*/
	public void SetMoveButtonVisible()
	{
		btnMoveUp.setVisibility(View.VISIBLE);
		btnMoveLeft.setVisibility(View.VISIBLE);
		btnMoveRight.setVisibility(View.VISIBLE);
		btnMoveDown.setVisibility(View.VISIBLE);
	}
	
	/**У׼ok��canle��ť��ʾ*/
	public void SetOkButtonVisible()
	{
		btnCorrCancle.setVisibility(View.VISIBLE);
		btnCorrOk.setVisibility(View.VISIBLE);
	}
	
	/**ȥ�������ʾ��*/
	public void RemoveOverlay()
	{
		if(ADD_LAYER_FLAG)
			mMapView.getOverlays().remove(correctionOverlay);
	}
	
	/**��Ӻ����ʾ��*/
	public void AddOverlay()
	{
		if(!ADD_LAYER_FLAG)
			mMapView.getOverlays().add(correctionOverlay);
	}
	
	/** ����У׼ */
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
	
	/** �˳�У׼ */
	public void ExitCorrection()
	{
		SetAllButtonGone();
		//ȥ��У׼��
		RemoveOverlay();
		mMapView.refresh();
		
		ADD_LAYER_FLAG = false;
		CORRECTION_START = false;
		
	}
	
	/**У׼˳������*/
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
	

	/**��ť��Ӧ����*/
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
		
		
		
		//΢�����ú���
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
