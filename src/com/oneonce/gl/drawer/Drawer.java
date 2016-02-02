package com.oneonce.gl.drawer;

import javax.microedition.khronos.opengles.GL10;

import com.oneonce.gl.utils.GLPaint;



/**
 * Abstract call, user must define a drawer extend it. 
 * the method of draw will be call by GL10_Renderer's onDrawFrame.<br/>
 * user can set the drawer is visible or not.<br/>
 * <br/>
 * 
 * @author oneonce
 * @date 2015.12.31
 * @version 1.0
 */
public abstract class Drawer {	
	private boolean mIsVisible = true;

	public synchronized void setVisible(boolean visible) {
		mIsVisible = visible;
	}

	public synchronized boolean isVisible() {
		return mIsVisible;
	}

	/**
	 *Draw the data<br/>
	 *
	 *@param gl
	 *@param width The width of view
	 *@param height The height of view
	 *
	 */
	public abstract void draw(GL10 gl, GLPaint glPaint, int width, int height);
}
