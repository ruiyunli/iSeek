package com.izzz.iseek.base;

import com.baidu.mapapi.map.OverlayItem;
import com.izzz.iseek.vars.StaticVar;

import android.view.View;
import android.view.View.OnClickListener;

public class BaseOnClickListener implements OnClickListener{

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if ( v.getId() == BaseMapMain.btnMoveUp.getId() )
			MoveCorration(StaticVar.MOVE_CORR_UP);
		else if ( v.getId() == BaseMapMain.btnMoveDown.getId() )
			MoveCorration(StaticVar.MOVE_CORR_DOWN);
		else if ( v.getId() == BaseMapMain.btnMoveLeft.getId() )
			MoveCorration(StaticVar.MOVE_CORR_LEFT);
		else if ( v.getId() == BaseMapMain.btnMoveRight.getId() )
			MoveCorration(StaticVar.MOVE_CORR_RIGHT);
	}
	
	//微调共用函数
	private void MoveCorration(int direction)
	{
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

}
