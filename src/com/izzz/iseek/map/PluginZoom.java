package com.izzz.iseek.map;

import com.baidu.mapapi.map.MapView;
import com.example.iseek.R;
import com.izzz.iseek.base.BaseMapMain;
import com.izzz.iseek.vars.StaticVar;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class PluginZoom {
	
	private MapView mMapView = null;
	
	private ImageButton btnZoomIn = null;		//zoom°´Å¥
	
	private ImageButton btnZoomOut = null;		//zoom°´Å¥
	
	private static final int ZOOM_MAX = 19;
	
	private static final int ZOOM_MIN = 3;
	
	public PluginZoom(MapView mMapView, ImageButton btnZoomIn,
			ImageButton btnZoomOut) {
		super();
		this.mMapView = mMapView;
		this.btnZoomIn = btnZoomIn;
		this.btnZoomOut = btnZoomOut;
		
		btnZoomIn.setOnClickListener(new PluginZoomOnClickListener());
		btnZoomOut.setOnClickListener(new PluginZoomOnClickListener());
	}

	private void ZoomIn(View v)
	{
		int currentLevel = mMapView.getZoomLevel();
		
		currentLevel += 1;
		
		mMapView.getController().setZoom(currentLevel);
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "zoom in to " + currentLevel);
		
		if(currentLevel >=  ZOOM_MAX)
		{
			btnZoomIn.setEnabled(false);
			Toast.makeText(v.getContext(), R.string.ToastZoomInMax, Toast.LENGTH_SHORT).show();
		}
		btnZoomOut.setEnabled(true);
	}
	
	private void ZoomOut(View v)
	{
		int currentLevel = BaseMapMain.mMapView.getZoomLevel();
		
		currentLevel -= 1;
		
		mMapView.getController().setZoom(currentLevel);
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "zoom out to " + currentLevel);
		
		if(currentLevel <=  ZOOM_MIN)
		{
			btnZoomOut.setEnabled(false);
			Toast.makeText(v.getContext(), R.string.ToastZoomInMin, Toast.LENGTH_SHORT).show();
		}
		btnZoomIn.setEnabled(true);
	}

	public class PluginZoomOnClickListener implements OnClickListener{
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId() == btnZoomIn.getId())
				ZoomIn(v);
			else if(v.getId() == btnZoomOut.getId())
				ZoomOut(v);
		}
	}
}
