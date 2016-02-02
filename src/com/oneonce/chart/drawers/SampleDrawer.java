package com.oneonce.chart.drawers;

import javax.microedition.khronos.opengles.GL10;

import com.oneonce.gl.drawer.Drawer;
import com.oneonce.gl.utils.GLPaint;

import android.content.Context;
import android.graphics.Color;





public class SampleDrawer extends Drawer {
	private Context mContext;
	public long times = 0;
	
	
	public SampleDrawer(Context context) {
		mContext = context;
	}

	@Override
	public void draw(GL10 gl, GLPaint glPaint, int width, int height) {
	    glPaint.drawText("opengl 20 fontsize", 0, 0, 20f, Color.MAGENTA);// draw text, left-top: (0, 0)

	    glPaint.drawText("rotate 90°", 200, 150, 30f, Color.BLACK, 90f);// draw text, left-top: (200, 150), rotate 90°
	    
	    glPaint.drawLine(0, 150, 600, 150, Color.BLACK);// draw line

	    float[] vertex1 = new float[] {350f, 10f, 350f, 100f,// line 1: (350 10), (350, 100)
	    								370f, 10f, 370f, 100f,// line 2: (370 10), (370, 100)
	    								390f, 10f, 390f, 100f,// line 3: (390 10), (390, 100)
	    								410f, 10f, 410f, 100f,// line 4: (410 10), (410, 100)
	    								430f, 10f, 430f, 100f,// line 5: (430 10), (430, 100)
	    								450f, 10f, 450f, 100f// line 6: (450 10), (450, 100)
	    								};
	    glPaint.drawLines(vertex1, Color.GREEN, 5f);// draw 6 lines, line-width: 5

	    float[] vertex2 = new float[] {10f, 260f, // point-1
	    								30f, 280f,// point-2
	    								60f, 270f,// point-3
	    								63f, 252f,// point-4
	    								600f, 170f,// point 5
	    								20f, 170f// point-6
	    								};
	    glPaint.drawLineStrip(vertex2, Color.BLUE, 3f);// 6 points, line-width: 3
	    
	    
	    // draw running times
	    float fontsize = 80f;
	    String strTimes = String.valueOf(times);
	    int strWidth = glPaint.getStringWidth(strTimes, fontsize);
	    // get the center of view
	    float x = (width - strWidth) / 2f;
	    float y = (height - fontsize) / 2f;
	    glPaint.drawText(strTimes, x, y, fontsize, Color.RED);
	}
}
