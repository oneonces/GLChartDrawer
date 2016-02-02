package com.oneonce.gl.gl10;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import com.oneonce.gl.drawer.Drawer;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.util.AttributeSet;
import android.view.SurfaceHolder;




/**
 * Abstract opengl surface view<br/>
 * Must extend GL10_SurfaceView, while use opengl draw something.<br/>
 * <br/>
 * 
 * @author lianghua
 * @date 2015.12.31
 * @version 1.0
 */
public abstract class GL10_SurfaceView extends GLSurfaceView implements SurfaceHolder.Callback {
	private final String TAG = GL10_SurfaceView.class.getSimpleName();
	private GL10_Renderer mRenderer = null;
	private Drawer mDrawer = null;
	private boolean mIsSpportDrawText = false;
	
	
	
	public GL10_SurfaceView(Context context, boolean isSpportDrawText) {
		this(context, null, isSpportDrawText);
	}

	public GL10_SurfaceView(Context context, AttributeSet attrs, boolean isSpportDrawText) {
		super(context, attrs);
		
		setIsSpportDrawText(isSpportDrawText);

		setZOrderOnTop(true);
		setBackgroundColor(Color.WHITE);
//		getHolder().setFormat(PixelFormat.TRANSLUCENT);

		// We want an 8888 pixel format because that's required for a translucent window.
		// And we want a depth buffer.
//		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		setEGLConfigChooser(new EGLConfigChooser() {
			
			@Override
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
				int[] attrList = new int[] { //  
						EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT, //
						EGL10.EGL_RED_SIZE, 8, //
						EGL10.EGL_GREEN_SIZE, 8, //
						EGL10.EGL_BLUE_SIZE, 8, //
						EGL10.EGL_DEPTH_SIZE, 16, //
						EGL10.EGL_SAMPLE_BUFFERS, 1,
						EGL10.EGL_SAMPLES, 1,
						EGL10.EGL_NONE //
				};  
				EGLConfig[] configOut = new EGLConfig[1];  
				int[] configNumOut = new int[1];  
				egl.eglChooseConfig(display, attrList, configOut, 1, configNumOut);  
				return configOut[0]; 
			}
		});
	}



	/**
	 * Must tell surface-view which drawer will use.<br/>
	 */
	protected boolean setDrawer(Drawer drawer) {
		if (null == drawer) {
			return false;
		}
		mDrawer = drawer;

		if (null == mRenderer) {
			mRenderer = new GL10_Renderer(drawer, false, isSpportDrawText());
			setRenderer(mRenderer);// set the renderer
			setRenderMode(RENDERMODE_WHEN_DIRTY);//
		}
		return true;
	}
	
	public Drawer getDrawer() {
		return mDrawer;
	}
	
	public synchronized boolean isBusy() {
		if (GL10_Renderer.STATUS_FREE == mRenderer.getStatus()) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * While new data is coming, call this method to render frames.<br/>
	 */
	public synchronized void refresh() {
//		if (!isBusy()) {
			requestRender();
//		}
	}
	
	/** 设置是否支持绘制文本 */
	public synchronized void setIsSpportDrawText(boolean supportFlag) {
		mIsSpportDrawText = supportFlag;
	}
	
	public synchronized boolean isSpportDrawText() {
		return mIsSpportDrawText;
	}
}
