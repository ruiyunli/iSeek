package com.izzz.iseek.map;

import com.example.iseek.R;
import com.izzz.iseek.base.BaseMapMain;
import com.izzz.iseek.vars.StaticVar;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MapZoomOnClickListener implements OnClickListener{

	private static final int ZOOM_MAX = 19;
	private static final int ZOOM_MIN = 3;
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == BaseMapMain.btnZoomIn.getId())
			ZoomIn(v);
		else if(v.getId() == BaseMapMain.btnZoomOut.getId())
			ZoomOut(v);
	}
	
	private void ZoomIn(View v)
	{
		int currentLevel = BaseMapMain.mMapView.getZoomLevel();
		
		currentLevel += 1;
		
		BaseMapMain.mMapController.setZoom(currentLevel);
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "zoom in to " + currentLevel);
		
		if(currentLevel >=  ZOOM_MAX)
		{
			BaseMapMain.btnZoomIn.setEnabled(false);
			Toast.makeText(v.getContext(), R.string.ToastZoomInMax, Toast.LENGTH_SHORT).show();
		}
		BaseMapMain.btnZoomOut.setEnabled(true);
	}
	
	private void ZoomOut(View v)
	{
		int currentLevel = BaseMapMain.mMapView.getZoomLevel();
		
		currentLevel -= 1;
		
		BaseMapMain.mMapController.setZoom(currentLevel);
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "zoom out to " + currentLevel);
		
		if(currentLevel <=  ZOOM_MIN)
		{
			BaseMapMain.btnZoomOut.setEnabled(false);
			Toast.makeText(v.getContext(), R.string.ToastZoomInMin, Toast.LENGTH_SHORT).show();
		}
		BaseMapMain.btnZoomIn.setEnabled(true);
	}

}
