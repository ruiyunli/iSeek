package com.izzz.iseek.base;

import com.baidu.mapapi.map.OverlayItem;
import com.example.iseek.R;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.vars.StaticVar;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class BaseOnClickListener implements OnClickListener{

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if ( v.getId() == BaseMapMain.btnMoveUp.getId() )
			MoveCorration(v, StaticVar.MOVE_CORR_UP);
		else if ( v.getId() == BaseMapMain.btnMoveDown.getId() )
			MoveCorration(v, StaticVar.MOVE_CORR_DOWN);
		else if ( v.getId() == BaseMapMain.btnMoveLeft.getId() )
			MoveCorration(v, StaticVar.MOVE_CORR_LEFT);
		else if ( v.getId() == BaseMapMain.btnMoveRight.getId() )
			MoveCorration(v, StaticVar.MOVE_CORR_RIGHT);
		else if ( v.getId() == BaseMapMain.btnViewSelect.getId())
			ChangeView();
		
	}
	
	//微调共用函数
	private void MoveCorration(View v, int direction)
	{
		if(BaseMapMain.corrPoint == null)
		{
			Toast.makeText(v.getContext(), R.string.ToastCorrPressFirst, Toast.LENGTH_LONG).show();
			return ;
		}
		
		switch(direction)
		{
		case StaticVar.MOVE_CORR_UP:
			BaseMapMain.corrPoint.setLatitudeE6(BaseMapMain.corrPoint.getLatitudeE6() + StaticVar.CORR_STEP);
			break;
		case StaticVar.MOVE_CORR_DOWN:
			BaseMapMain.corrPoint.setLatitudeE6(BaseMapMain.corrPoint.getLatitudeE6() - StaticVar.CORR_STEP);
			break;
		case StaticVar.MOVE_CORR_LEFT:
			BaseMapMain.corrPoint.setLongitudeE6(BaseMapMain.corrPoint.getLongitudeE6() - StaticVar.CORR_STEP);
			break;
		case StaticVar.MOVE_CORR_RIGHT:
			BaseMapMain.corrPoint.setLongitudeE6(BaseMapMain.corrPoint.getLongitudeE6() + StaticVar.CORR_STEP);
			break;
		}
		BaseMapMain.corrItem =new OverlayItem(BaseMapMain.corrPoint, "title", "snippet");
		BaseMapMain.correctionOverlay.refreshItem(BaseMapMain.corrItem);
		BaseMapMain.mMapView.refresh();
		BaseMapMain.logText.setText("x: unknown" + " y: unknown" 
                + '\n' + " latitude: " + BaseMapMain.corrPoint.getLatitudeE6()
                +" longitude: " + BaseMapMain.corrPoint.getLongitudeE6());
	}
	
	/**
	 * 切换视图
	 */
	private void ChangeView()
	{
		if(BaseMapMain.btnViewSelect.getContentDescription().equals
				(IseekApplication.getInstance().getResources().getString(R.string.ImageViewSatellite)))
		{
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "satellite view to traffic view");
			BaseMapMain.mMapView.setTraffic(true);
			BaseMapMain.mMapView.setSatellite(false);
			BaseMapMain.mMapView.refresh();
			BaseMapMain.btnViewSelect.setImageResource(R.drawable.icon_button_satellite);
			BaseMapMain.btnViewSelect.setContentDescription(
					IseekApplication.getInstance().getResources().getString(R.string.ImageViewTraffic));
		}
		else
		{
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "traffic view to satellite view");
			BaseMapMain.mMapView.setTraffic(false);
			BaseMapMain.mMapView.setSatellite(true);
			BaseMapMain.mMapView.refresh();
			BaseMapMain.btnViewSelect.setImageResource(R.drawable.icon_button_traffic);
			BaseMapMain.btnViewSelect.setContentDescription(
			IseekApplication.getInstance().getResources().getString(R.string.ImageViewSatellite));
		
		}
	}

}
