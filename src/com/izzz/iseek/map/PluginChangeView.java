package com.izzz.iseek.map;

import com.baidu.mapapi.map.MapView;
import com.example.iseek.R;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.base.BaseMapMain;
import com.izzz.iseek.vars.StaticVar;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class PluginChangeView {
	
	private Context mContext = null;
	
	private ImageButton btnViewSelect = null; 	// ”Õº«–ªª
	
	private MapView mMapView = null;	
	
	public PluginChangeView(Context mContext, ImageButton btnViewSelect,
			MapView mMapView) {
		super();
		this.mContext = mContext;
		this.btnViewSelect = btnViewSelect;
		this.mMapView = mMapView;
		
		btnViewSelect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ChangeView();
			}
		});
	}
	
	private void ChangeView()
	{
		if(btnViewSelect.getContentDescription().equals
				(IseekApplication.getInstance().getResources().getString(R.string.BtnSatellite)))
		{
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "satellite view to traffic view");
			
			mMapView.setTraffic(true);
			mMapView.setSatellite(false);
			mMapView.refresh();
			btnViewSelect.setImageResource(R.drawable.icon_button_satellite);
			btnViewSelect.setContentDescription(
					IseekApplication.getInstance().getResources().getString(R.string.BtnTraffic));
		}
		else
		{
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "traffic view to satellite view");
			
			mMapView.setTraffic(false);
			mMapView.setSatellite(true);
			mMapView.refresh();
			btnViewSelect.setImageResource(R.drawable.icon_button_traffic);
			btnViewSelect.setContentDescription(mContext.getResources().getString(R.string.BtnSatellite));
		}
	}
	


}
