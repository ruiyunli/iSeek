package com.izzz.iseek.mapcontrol;

import android.graphics.drawable.Drawable;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.OverlayItem;

public class CorrectionOverlay extends ItemizedOverlay<OverlayItem>{

	public OverlayItem mGeoPt = null;
	
	
	public CorrectionOverlay(Drawable marker) {
		super(marker);
		// TODO Auto-generated constructor stub
//		GeoPoint corrPoint = new GeoPoint((int)(34.235697*1E6), (int)(108.914238*1E6));
		mGeoPt = new OverlayItem(null,null,null);
		boundCenter(mGeoPt);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return mGeoPt;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	public void refreshItem(OverlayItem item)
	{
		mGeoPt = item;
		boundCenter(mGeoPt);
		System.out.println("refresh item");
		populate();
	}
	
	

}
