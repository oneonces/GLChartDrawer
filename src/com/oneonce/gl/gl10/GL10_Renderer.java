package com.oneonce.gl.gl10;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.oneonce.gl.drawer.Drawer;
import com.oneonce.gl.utils.GLPaint;

import android.opengl.GLSurfaceView;




/**
 * opengl renderer<br/>
 * While data is dirty, onDrawFrame will call the draw method of all drawers.<br/>
 * <br/>
 * 
 * @author oneonce
 * @date 2015.12.31
 * @version 1.0
 */
public class GL10_Renderer implements GLSurfaceView.Renderer {
	private final String TAG = GL10_Renderer.class.getSimpleName();
	
	public final static int STATUS_FREE = 0;// 空闲
	public final static int STATUS_BUSY = 1;// 正在绘制
	private int mStatus = STATUS_FREE;
	private boolean mTransBackground;// is the background transport
	private Drawer mDrawer;
	private GLPaint mGLPaint = null;
	private int mViewWidth = 0;
	private int mViewHeight = 0;
	private boolean mIsSpportDrawText = false;

	
	
	/**
	 * drawer the drawer
	 * transBackground if false the color will be clear as white
	 * isSpportDrawText if true the drawer can be draw text/string
	 */
	public GL10_Renderer(Drawer drawer, boolean transBackground, boolean isSpportDrawText) {
		mTransBackground = transBackground;
		mDrawer = drawer;
		
		mIsSpportDrawText = isSpportDrawText;
		setStatus(STATUS_FREE);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		if (mTransBackground) {
			gl.glClearColor(0, 0, 0, 1.0f);// blank
		}
		else {
			gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);// white
		}
		gl.glDisable(GL10.GL_DITHER);
//		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

//		 gl.glEnable(GL10.GL_CULL_FACE);
//		 gl.glShadeModel(GL10.GL_SMOOTH);
		 
//		 gl.glEnable(GL10.GL_DEPTH_TEST);// enable depth test
//		 gl.glClearDepthf(1.0f);
//		 gl.glDepthFunc(GL10.GL_LEQUAL);// the type of depth test

//		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_BLEND);
		gl.glEnable(GL10.GL_LINE_SMOOTH);// enable line-smooth

		gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		mViewWidth = width;
		mViewHeight = height;
		gl.glViewport(0, 0, width, height);

		if (null == mGLPaint) {
			mGLPaint = new GLPaint(gl, width, height, mIsSpportDrawText);
		}
		else {
			mGLPaint.reset(gl, width, height, mIsSpportDrawText);
		}

		float ratio = (float) width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);

//		gl.glMatrixMode(GL10.GL_PROJECTION); //6
//		gl.glLoadIdentity();
//		GLU.gluPerspective(gl, 0f, (float)width / (float)height, 1.0f, 20.0f);
//		gl.glMatrixMode(GL10.GL_MODELVIEW);
//		gl.glLoadIdentity();
//		GLU.gluLookAt(gl, 0, 0, 1f,   0, 0, 1f,   0f, 0f, 0.0f);// http://blog.csdn.net/ivan_ljf/article/details/8764737

//		gl.glMatrixMode(GL10.GL_PROJECTION);
//		gl.glLoadIdentity();
//		GLU.gluOrtho2D(gl, 0, width, height, 0);
//		gl.glMatrixMode(GL10.GL_MODELVIEW);
//		gl.glLoadIdentity();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		synchronized (gl) {
//			Log.e(TAG, "onDrawFrame+++++++++++++++++++++");
			setStatus(STATUS_BUSY);

			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glTranslatef(0, 0, -3f);

			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);// enable the vertex

			if (null == mDrawer) {
				return;
			}
			else if (mDrawer.isVisible()) {
				mDrawer.draw(gl, mGLPaint, mViewWidth, mViewHeight);
				mGLPaint.drawBackground();
			}

			gl.glFlush();
			gl.glFinish();
			//gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			mGLPaint.deleteTexture();// Delete all texture to release memory buffer
			mGLPaint.clearBackground();

			setStatus(STATUS_FREE);
//			Log.e(TAG, "onDrawFrame---------------------");
		}
	}

	public synchronized int getStatus() {
		return mStatus;
	}

	public synchronized void setStatus(int status) {
		mStatus = status;
	}
}
