package com.izzz.iseek.map;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.iseek.R;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.base.BaseMapMain;
import com.izzz.iseek.vars.StaticVar;

public class Correction {
	
	private Context mContext = null;
	
	public MapView mMapView = null;

	public CorrectionOverlay correctionOverlay = null;	//У׼�ñ���
	
	public GeoPoint corrPoint = null;					//У׼�ñ���
	
	public OverlayItem corrItem = null;					//У׼�ñ���	
	
	public ImageButton btnMoveUp    = null;				//΢��button
	
	public ImageButton btnMoveDown  = null;				//΢��button
	
	public ImageButton btnMoveLeft  = null;				//΢��button
	
	public ImageButton btnMoveRight = null;				//΢��button
	
	public Button btnCorrOk = null;						//У׼��������ť
	
	public Button btnCorrCancle = null;					//У׼��������ť
	
	public boolean ADD_LAYER_FLAG = false;				//�Ƿ�����и��ǲ�
	
	public boolean CORRECTION_START = false;			//��Ҫ������ontouch��Ӧ�о����Ƿ���ʾ���

	public Correction(Context mContext, MapView mMapView, ImageButton btnMoveUp,
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
		
		
		btnCorrCancle.setOnClickListener(new CorrOnClickListener());
		btnMoveUp.setOnClickListener(new CorrOnClickListener());
		btnMoveDown.setOnClickListener(new CorrOnClickListener());
		btnMoveLeft.setOnClickListener(new CorrOnClickListener());
		btnMoveRight.setOnClickListener(new CorrOnClickListener());
		
		correctionOverlay = new CorrectionOverlay(mContext.getResources().getDrawable(R.drawable.icon_mapselect));
	}
	
	
	public void SetAllButtonGone()
	{
		SetMoveButtonGone();
		SetOkButtonGone();
	}
	
	public void SetMoveButtonGone()
	{
		btnMoveUp.setVisibility(View.GONE);
		btnMoveLeft.setVisibility(View.GONE);
		btnMoveRight.setVisibility(View.GONE);
		btnMoveDown.setVisibility(View.GONE);
	}
	
	public void SetOkButtonGone()
	{
		btnCorrCancle.setVisibility(View.GONE);
		btnCorrOk.setVisibility(View.GONE);
	}
	
	public void SetAllButtonVisible()
	{
		SetMoveButtonVisible();
		SetOkButtonVisible();
	}
	
	public void SetMoveButtonVisible()
	{
		btnMoveUp.setVisibility(View.VISIBLE);
		btnMoveLeft.setVisibility(View.VISIBLE);
		btnMoveRight.setVisibility(View.VISIBLE);
		btnMoveDown.setVisibility(View.VISIBLE);
	}
	
	public void SetOkButtonVisible()
	{
		btnCorrCancle.setVisibility(View.VISIBLE);
		btnCorrOk.setVisibility(View.VISIBLE);
	}
	
	public void RemoveOverlay()
	{
		if(ADD_LAYER_FLAG)
			mMapView.getOverlays().remove(correctionOverlay);
	}
	
	public void AddOverlay()
	{
		if(!ADD_LAYER_FLAG)
			mMapView.getOverlays().add(correctionOverlay);
	}
	
	public void ExitCorrection()
	{
		SetAllButtonGone();
		//ȥ��У׼��
		RemoveOverlay();
		mMapView.refresh();
		
		ADD_LAYER_FLAG = false;
		CORRECTION_START = false;
		
	}

	
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
			
		}
		
		//΢�����ú���
		private void MoveCorration(View v, int direction)
		{
			if(corrPoint == null)
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
				BaseMapMain.logText.setText("x: unknown" + " y: unknown" 
		                + "\n" + " latitude: " + corrPoint.getLatitudeE6()
		                +" longitude: " + corrPoint.getLongitudeE6());
		}
	}
	
	
	
	
}
