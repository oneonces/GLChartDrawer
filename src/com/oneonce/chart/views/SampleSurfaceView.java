package com.oneonce.chart.views;

import com.oneonce.chart.drawers.SampleDrawer;
import com.oneonce.gl.gl10.GL10_SurfaceView;

import android.content.Context;



/**
 * A sample for draw chart<br/>
 * Usage:<br/>
 * 
 * Activity -> onCreate(): 
 * 
 * SampleDrawer drawer = new SampleDrawer(this);
 * SampleSurfaceView sampleSrufaceView = new SampleSurfaceView(this, drawer);
 * 
 * setContentView(sampleSrufaceView);
 */

public class SampleSurfaceView extends GL10_SurfaceView {
	private SampleDrawer mDrawer = null;

	public SampleSurfaceView(Context context, SampleDrawer drawer) {
		super(context, true);
		mDrawer = drawer;
		
		setDrawer(drawer);
	}
	
	public void updateRunTimes(long times) {
		mDrawer.times = times;
		refresh();
	}
}
