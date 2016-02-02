package com.oneonce.gl.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.opengl.GLUtils;







/**
 * 1. opengl no draw text/string method, use this class for draw.<br/>
 * 2. this class provide draw-line/s, draw-rect, draw-bitmap method too.<br/>
 * <br/>
 * 
 * @author lianghua
 * @date 2015.12.31
 * @version 1.0
 */
public class GLPaint {
	/** draw a line need 2 coordinates(4 point) */
	public static final int LINES_POINTS = 4;// draw lines need 2N points
	public static final int STRIP_POINTS = 2;// draw line-strip need N+1 points

	// totate angle
	public static final float ROTATE_ANGLE_0 = 0f;// Horizontal(The orientation of string: Left -> right)

	private boolean mIsSpportDrawText = false;
	private GL10 mGL10 = null;
	private int mViewWidth = 0;
	private int mViewHeight = 0;
	private int[] mSupportLineWidth = null;
	private int[] mSmoothSupportLineWidth = null;
	
	private Rect mBounds = new Rect();
	private Paint mPaint = new Paint();
	private Bitmap mBackground = null;
	private Canvas mCanvas = null;
	private List<Integer> mTexturesList = null;// save all textures's id for delete
	
	
	public double stringToDouble(String content) {
		return stringToDouble(content, 0);
	}
	
	public double stringToDouble(String content, int fractionDigits) {
		NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
		format.setMaximumFractionDigits(fractionDigits);// 不显示小数点后面的数据
		
		try {
			return format.parse(content.trim()).doubleValue();
		}
		catch (ParseException e) {
			e.printStackTrace();
			return 0.0;
		}
	}

	/**
	 * before use GLDrawer, must set the view's size.<br/>
	 * <br/>
	 * @param viewWidth the width of the view(surface view)
	 * @param viewHeight the height of the view(surface view)
	 */
	public GLPaint(GL10 gl, int viewWidth, int viewHeight, boolean isSpportDrawText) {
		reset(gl, viewWidth, viewHeight, isSpportDrawText);
	}
	
	public void reset(GL10 gl, int viewWidth, int viewHeight, boolean isSpportDrawText) {
		mGL10 = gl;
		mViewWidth = viewWidth;
		mViewHeight = viewHeight;

		setIsSpportDrawText(isSpportDrawText);
	
		if (isSpportDrawText()) {
//			int pow2Width = pow2(viewWidth);
//			int pow2Height = pow2(viewHeight);
			createBackground(viewWidth, viewHeight);
		}
	}

	public void createBackground(int viewWidth, int viewHeight) {
		int pow2Width = pow2(viewWidth);
		int pow2Height = pow2(viewHeight);
		mBackground = Bitmap.createBitmap(pow2Width, pow2Height, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBackground);
		clearCanvas();
		setPaint();
	}
	
	public void clearBackground() {
		if (null == mBackground) {
			return;
		}
		clearCanvas();
	}
	
	public void drawBackground() {
		if (null == mBackground) {
			return;
		}
		
//		int w = mCanvas.getWidth();
//		int h = mCanvas.getHeight();
		drawBitmap(0, 0, mBackground);
	}
	
	private void clearCanvas() {
		if (null == mBackground) {
			return;
		}
		mBackground.eraseColor(Color.TRANSPARENT);
//		mCanvas.drawColor(Color.TRANSPARENT);
//		mBackground.eraseColor(Color.GREEN);
//		mCanvas.drawColor(Color.GREEN);
//		mCanvas.drawColor(Color.GREEN);
	}

	public void deleteTexture() {
		if (null == mTexturesList)
			return;
		
		int cnt = mTexturesList.size();
		if (cnt > 0) {
			int[] textures = new int[cnt];
			for (int i = 0; i < cnt; i++) {
				int id = mTexturesList.get(i);
				textures[i] = id;
			}
			mGL10.glDeleteTextures(cnt, textures, 0);
			textures = null;
			mTexturesList = null;
		}
	}
	
	public int getStringWidth(String str, float fontSize) {
		mPaint.setTextSize(fontSize);
		mPaint.getTextBounds(str, 0, str.length(), mBounds);
//		float w = mPaint.measureText(str);
		return mBounds.width();
	}

	public int getStringHeight(String str, float fontSize) {
		mPaint.setTextSize(fontSize);
		FontMetrics fontMetrics = mPaint.getFontMetrics();
		mPaint.getTextBounds(str, 0, str.length(), mBounds);
		return (int) (mBounds.height() - (fontMetrics.ascent - fontMetrics.top));
//		return (int) (fontMetrics.descent - fontMetrics.ascent);
	}

	private void setPaint() {
//		Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
//		mPaint.setTypeface(typeface);
		mPaint.setTypeface(Typeface.DEFAULT);// default typeface
		mPaint.setAntiAlias(true);// AntiAlias
		mPaint.setTextAlign(Paint.Align.LEFT);// alignment - left
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
//		mPaint.setStrokeWidth(2f);
		mPaint.setDither(true); //获取跟清晰的图像采样
		mPaint.setFilterBitmap(true);//过滤一些
	}

	/**
	 * Adjust the size of font, refer to the limit-width<br/>
	 * 
	 * @param str
	 * @param currFontSize Current font size
	 * @param limitWidth  
	 */
	public float adjustFontSize(String str, float currFontSize, float limitWidth) {
		float ratio = 1;
		float fontSize = currFontSize;
		int currStrWidth = getStringWidth(str, fontSize);
//		double hwratio = getStringHeight(str, fontSize) / getStringWidth(str, fontSize);

		while (currStrWidth > limitWidth) {// current string width is bigger than limit width
			ratio = limitWidth / currStrWidth;
//			fontSize = (float) (fontSize * ratio * (1 + hwratio));
			fontSize = fontSize * ratio;
			currStrWidth = getStringWidth(str, fontSize);
		}
		
		return fontSize;
	}
	
	public synchronized void setIsSpportDrawText(boolean isSupport) {
		mIsSpportDrawText = isSupport;
	}

	/** 是否支持绘制文本 */
	public synchronized boolean isSpportDrawText() {
		return mIsSpportDrawText;
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////                                                                                     Text                                                                                     //////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	

	/**
	 * Default color is black<br/>
	 * Default orientation is horizontal<br/>
	 */
	public void drawText(String content, float x, float y, float fontSize) {
		drawText(content, x, y, fontSize, Color.BLACK, GLPaint.ROTATE_ANGLE_0);
	}

	public void drawText(String content, float x, float y, float fontSize, int fontColor) {
		drawText(content, x, y, fontSize, fontColor, GLPaint.ROTATE_ANGLE_0);
	}

	public void drawText(String content, float x, float y, float fontSize, int fontColor, float rotateAngle) {
		if (null == mCanvas || null == mPaint || null == content || content.trim().isEmpty())
			return;

		mPaint.setTextSize(fontSize);
		mPaint.setColor(fontColor);
		
		FontMetrics fontMetrics = mPaint.getFontMetrics();
		float textTopPos = y - fontMetrics.ascent - (fontMetrics.ascent - fontMetrics.top);

		if (GLPaint.ROTATE_ANGLE_0 == rotateAngle) {
			// To do nothing
		}
		else {
			mCanvas.rotate(-rotateAngle, x, y);// 反向旋转画布
		}
		
		mCanvas.drawText(content, x, textTopPos, mPaint);// 绘制文本
		
		if (GLPaint.ROTATE_ANGLE_0 == rotateAngle) {
			// To do nothing
		}
		else {
			mCanvas.rotate(rotateAngle, x, y);// 正向选择到原始位置
		}
	}

	public void drawBitmap(float x, float y, Bitmap bitmap) {
		drawTexture(x, y, bitmap);
	}

	private void drawTexture(float x, float y, Bitmap bitmap) {
		if (null == mTexturesList) {
			mTexturesList = new ArrayList<Integer>();
		}

		mGL10.glMatrixMode(GL10.GL_PROJECTION);
		mGL10.glLoadIdentity();
//		mGL10.glOrthof(0, (mViewWidth < bitmap.getWidth())? bitmap.getWidth():mViewWidth, (mViewHeight < bitmap.getHeight())? bitmap.getHeight():mViewHeight, 0, 0.0f, 10);// will compress the bitmap
		mGL10.glOrthof(0, mViewWidth, mViewHeight, 0, 0.0f, 10);// when the width of bitmap is bigger than view, will display part of it.

		enableTexture();
		bindTextures(bitmap);

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int pow2Width = pow2(width);
		int pow2Height = pow2(height);
		float MaxW = width / (float) pow2Width;
		float MaxH = height / (float) pow2Height;

//		float alpha = (float)((fontColor & 0xff000000) >> 24) / 255.0f;
//		float red = (float)((0x00ff0000 & fontColor) >> 16) / 255.0f;
//		float green = (float)((0x0000ff00 & fontColor) >> 8) / 255.0f;
//		float blue = (float)(0x000000ff & fontColor) / 255.0f;
//		mGL10.glColor4f(red, green, blue, 1.0f);

		FloatBuffer pointer = getVertex(x, y, width, height);
		FloatBuffer coord = getTexturesCoord(0, 0, MaxW, MaxH);
		
		mGL10.glVertexPointer(2, GL10.GL_FLOAT, 0, pointer);
		mGL10.glTexCoordPointer(2, GL10.GL_FLOAT, 0, coord);
		mGL10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		disableTexture();
	}

	/**
	 * binding the textures<br/>
	 * refer to: android-sdk\samples\android-XXX\ApiDemos\src\com\example\android\apis\graphics\spritetext
	 */
	private void bindTextures(Bitmap bitmap) {
		int[] textures = new int[1];
		mGL10.glGenTextures(1, textures, 0);
		mGL10.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		mTexturesList.add(textures[0]);

		mGL10.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		mGL10.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		mGL10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		mGL10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
//		mGL10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
//		mGL10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		
//		mGL10.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
//		mGL10.glActiveTexture(GL10.GL_TEXTURE0);
        
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		
//		bitmap.recycle();
//		bitmap = null;
	}

	private void enableTexture() {
		mGL10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		mGL10.glEnable(GL10.GL_BLEND);

		mGL10.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		mGL10.glEnable(GL10.GL_TEXTURE_2D);
//		mGL10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	}
	
	private void disableTexture() {
//		mGL10.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		mGL10.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		mGL10.glDisable(GL10.GL_TEXTURE_2D);
		mGL10.glDisable(GL10.GL_BLEND);
	}
	
	public Bitmap createTextureImage(String content, float fontSize, int fontColor, float rotateAngle) {
		int strWidth = 0;
		int strHeight = 0;
		int pow2Width = 0;
		int pow2Height = 0;
		Bitmap bitmap = null;
		
		mPaint.setTextSize(fontSize);
		mPaint.setColor(fontColor);

		mPaint.getTextBounds(content, 0, content.length(), mBounds);
		FontMetricsInt fontMetricsInt = mPaint.getFontMetricsInt();
		strWidth = mBounds.right - mBounds.left + 1;
		strHeight = mBounds.bottom - mBounds.top;
		pow2Width = (int) pow2(strWidth);
		pow2Height = (int) pow2(strHeight);

		bitmap = Bitmap.createBitmap(pow2Width, pow2Height, Bitmap.Config.ARGB_8888);
		
//		bitmap.eraseColor(Color.TRANSPARENT);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.TRANSPARENT);
//		canvas.drawColor(Color.GREEN);

		float x = 0;  
        float y = (pow2Height - fontMetricsInt.ascent - fontMetricsInt.descent) / 2;

		canvas.drawText(content, x, y, mPaint);
		
		if (GLPaint.ROTATE_ANGLE_0 == rotateAngle) {
			// To do nothing
		}
		else {
			bitmap = rotateBitmap(bitmap, rotateAngle);
		}

		return bitmap;
	}

	private FloatBuffer getTexturesCoord(float x, float y, float maxW, float maxH) {
		float[] v = new float[] {
				x, y,
				maxW, y,
				x, maxH,
				maxW, maxH
		};
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8*4);
		byteBuffer.order(ByteOrder.nativeOrder());
		FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
		floatBuffer.put(v);
		floatBuffer.position(0);
		
		return floatBuffer;
	}
	
	private FloatBuffer getVertex(float x, float y, float width, float height) {
		float[] v = new float[] {
				x, y,
				x+width, y,
				x, y+height,
				x+width, y+height
		};
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8*4);
		byteBuffer.order(ByteOrder.nativeOrder());
		FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
		floatBuffer.put(v);
		floatBuffer.position(0);
		
		return floatBuffer;
	}

	private int pow2(int size) {
		int small = (int) (Math.log(size) / Math.log(2));
		int v = (1 << small);
		if (v >= size)
			return (1 << small);
		else
			return 1 << (small + 1);
	}
	
	private Bitmap rotateBitmap(Bitmap srcBitmap, float rotateAngle) {
		Matrix matrix = new Matrix();
		
		matrix.postRotate(rotateAngle);
		Bitmap destBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
		
		return destBitmap;
	}


	

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////                                                              Polygon                                                             //////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Default color is black<br/>
	 * line-width is 1<br/>
	 */
	public void drawLine(float x1, float y1, float x2, float y2) {
		drawLine(x1, y1, x2, y2, 1);
	}
	
	public void drawLine(float x1, float y1, float x2, float y2, int lineColor) {
		drawLine(x1, y1, x2, y2, lineColor, 1);
	}
	
	/// Default color is black
	public void drawLine(float x1, float y1, float x2, float y2, float lineWidth) {
		drawLine(x1, y1, x2, y2, Color.BLACK, lineWidth);
	}

	/**
	 * draw a line<br/>
	 * <br/>
	 * @param gl opengl
	 * @param x1 start point of x
	 * @param y1 start point of y
	 * @param x2 end point of x
	 * @param y2 end point of y
	 * @param lineColor the color of line, format: ARGB
	 * @param viewWidth the width of the view
	 * @param viewHeight the height of the view
	 */
	public void drawLine(float x1, float y1, float x2, float y2, int lineColor, float lineWidth) {
		drawPolygon(new float[]{x1, y1, x2, y2}, GL10.GL_LINES, lineColor, lineWidth);
	}
	
	/**
	 * Default color is black<br/>
	 * Default line width is 1<br/>
	 * data format: X1, Y1, X2, Y2[, X3, Y3, X4, Y4[, ...[, X(n), Y(n), X(n+1), Y(n+1)[,...]]]]<br/>
	 * the minimum of point(x,y) pairs is 2<br/>
	 */
	public void drawLines(float[] vertex) {
		drawLines(vertex, 1);
	}
	
	/**
	 * Default line width is 1<br/>
	 * data format: X1, Y1, X2, Y2[, X3, Y3, X4, Y4[, ...[, X(n), Y(n), X(n+1), Y(n+1)[,...]]]]<br/>
	 * the minimum of point(x,y) pairs is 2<br/>
	 */
	public void drawLines(float[] vertex, int lineColor) {
		drawLines(vertex, lineColor, 1);
	}
	
	/**
	 * Default color is black<br/>
	 * data format: X1, Y1, X2, Y2[, X3, Y3, X4, Y4[, ...[, X(n), Y(n), X(n+1), Y(n+1)[,...]]]]<br/>
	 * the minimum of point(x,y) pairs is 2<br/>
	 */
	public void drawLines(float[] vertex, float lineWidth) {
		drawLines(vertex, Color.BLACK, lineWidth);
	}
	
	/**
	 * data format: X1, Y1, X2, Y2[, X3, Y3, X4, Y4[, ...[, X(n), Y(n), X(n+1), Y(n+1)[,...]]]]<br/>
	 * the minimum of point(x,y) pairs is 2<br/>
	 */
	public void drawLines(float[] vertex, int lineColor, float lineWidth) {
		drawPolygon(vertex, GL10.GL_LINES, lineColor, lineWidth);
	}
	
	/**
	 * Default line width is 1<br/>
	 * data format: X1, Y1, [, ...[, X(n), Y(n)[,...]]]]<br/>
	 * the minimum of point(x,y) pairs is 2<br/>
	 */
	public void drawLineStrip(float[] vertex, int lineColor) {
		drawPolygon(vertex, GL10.GL_LINE_STRIP, lineColor, 1);
	}
	
	/**
	 * data format: X1, Y1, [, ...[, X(n), Y(n)[,...]]]]<br/>
	 * the minimum of point(x,y) pairs is 2<br/>
	 */
	public void drawLineStrip(float[] vertex, float lineWidth) {
		drawPolygon(vertex, GL10.GL_LINE_STRIP, Color.BLACK, lineWidth);
	}
	
	/**
	 * data format: X1, Y1, [, ...[, X(n), Y(n)[,...]]]]<br/>
	 * the minimum of point(x,y) pairs is 2<br/>
	 */
	public void drawLineStrip(float[] vertex, int lineColor, float lineWidth) {
		drawPolygon(vertex, GL10.GL_LINE_STRIP, lineColor, lineWidth);
	}
	
	/**
	 * Default line width is 1<br/>
	 * data format: X1, Y1, X2, Y2[, X3, Y3[, ...[, X(n), Y(n)[,...]]]]<br/>
	 * the minimum of point(x,y) pairs is 2<br/>
	 */	
	public void drawLineLoop(float[] vertex, int lineColor) {
		drawPolygon(vertex, GL10.GL_LINE_LOOP, lineColor, 1);
	}
	
	/**
	 * Default line color is black<br/>
	 * data format: X1, Y1, X2, Y2[, X3, Y3[, ...[, X(n), Y(n)[,...]]]]<br/>
	 * the minimum of point(x,y) pairs is 2<br/>
	 */	
	public void drawLineLoop(float[] vertex, float lineWidth) {
		drawPolygon(vertex, GL10.GL_LINE_LOOP, Color.BLACK, lineWidth);
	}
	
	
	/**
	 * data format: X1, Y1, X2, Y2[, X3, Y3[, ...[, X(n), Y(n)[,...]]]]<br/> 
	 */
	public void drawLineLoop(float[] vertex, int lineColor, float lineWidth) {
		drawPolygon(vertex, GL10.GL_LINE_LOOP, lineColor, lineWidth);
	}
	
	/**
	 * Default line color is black<br/>
	 * Default line width is 1<br/>
	 * Default no fill color<br/>
	 */	
	public void drawRect(float x, float y, float width, float height) {
		drawRect(x, y, width, height, Color.BLACK, false, 1);
	}

	/**
	 * Default line color is black<br/>
	 */	
	public void drawRect(float x, float y, float width, float height, boolean fillColor, float lineWidth) {
		drawRect(x, y, width, height, Color.BLACK, fillColor, lineWidth);
	}
	
	/**
	 * Default line width is 1<br/>
	 */	
	public void drawRect(float x, float y, float width, float height, int lineColor, boolean fillColor) {
		drawRect(x, y, width, height, lineColor, fillColor, 1);
	}

	public void drawRect(float x, float y, float width, float height, int lineColor, boolean fillColor, float lineWidth) {
		if (fillColor)
			drawPolygon(new float[]{x, y, x, y+height, x+width, y, x+width, y+height}, lineColor, true, lineWidth);
		else
			drawPolygon(new float[]{x, y, x, y+height, x+width, y+height, x+width, y}, lineColor, false, lineWidth);
	}

	/**
	 * @param gl opengl.
	 * @param vertex datas, data format: x1, y1, x2, y2[, x3, y3, x4, y4[, ...]]
	 * @param color the color of polygon.
	 * @param fillColor if draw rectangle, triangle, or other polygon shape can fill the color of background.
	 * @param lineWidth if draw rectangle, triangle, or other polygon shape can set the line-width.
	 */	
	private void drawPolygon(float [] vertex, int color, boolean fillColor, float lineWidth) {
		if (fillColor) {
			drawPolygon(vertex, GL10.GL_TRIANGLE_STRIP, color, lineWidth);
		}
		else {
			drawPolygon(vertex, GL10.GL_LINE_LOOP, color, lineWidth);
		}
	}

	/**
	 * use this interface can draw almost things.<br/>
	 * 
	 * @param gl opengl.
	 * @param vertex datas, data format: x1, y1, x2, y2[, x3, y3, x4, y4[, ...]]
	 * @param mode the mode for how to draw.
	 * @param color the color of polygon.
	 * @param lineWidth if draw rectangle, triangle, or other polygon shape can set the line-width.
	 */	
	public void drawPolygon(float[] vertex, int mode, int color, float lineWidth) {
		mGL10.glMatrixMode(GL10.GL_PROJECTION);
		mGL10.glLoadIdentity();
		
		mGL10.glOrthof(0, mViewWidth, mViewHeight, 0, 0f, 10f);

		mGL10.glDisable(GL10.GL_BLEND);
//		mGL10.glEnable(GL10.GL_BLEND);
//		mGL10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);//
		
		mGL10.glEnable(GL10.GL_POINT_SMOOTH);
		mGL10.glHint(GL10.GL_POINT_SMOOTH_HINT, GL10.GL_NICEST);// Make round points, not square points
		mGL10.glEnable(GL10.GL_LINE_SMOOTH);
		mGL10.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);// Antialias the lines

//		mGL10.glEnable(mode);
		
		setLineWidth(lineWidth);
//		mGL10.glLineWidth(lineWidth);

		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertex.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		FloatBuffer vertexs = byteBuffer.asFloatBuffer();
		vertexs.put(vertex);
		vertexs.position(0);

		float alpha = (float)((color & 0xff000000) >> 24) / 255.0f;
		float red = (float)((0x00ff0000 & color) >> 16) / 255.0f;
		float green = (float)((0x0000ff00 & color) >> 8) / 255.0f;
		float blue = (float)(0x000000ff & color) / 255.0f;
		mGL10.glColor4f(red, green, blue, 1.0f);

		mGL10.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexs);
		mGL10.glDrawArrays(mode, 0, vertex.length / 2);

		// restore
//		mGL10.glDisable(mode);
		mGL10.glLineWidth(1.0f);
	}

	private void setLineWidth(float lineWidth) {
		if (null == mSupportLineWidth) {// If can set the line width depends on whether the platform to achieve
			mSupportLineWidth = new int[2];
			mGL10.glGetIntegerv(GL10.GL_ALIASED_LINE_WIDTH_RANGE, mSupportLineWidth, 0);
		}
		
		if (null == mSmoothSupportLineWidth) {
			mSmoothSupportLineWidth = new int[2];
			mGL10.glGetIntegerv(GL10.GL_SMOOTH_LINE_WIDTH_RANGE, mSmoothSupportLineWidth, 0);
		}
		
		if (lineWidth > 1) {
			if (mSmoothSupportLineWidth[1] > 1) {
				if (mSmoothSupportLineWidth[1] >= lineWidth) {
					mGL10.glLineWidth(lineWidth);
				}
				else {
					mGL10.glLineWidth(mSmoothSupportLineWidth[1]);
				}
			}
			else if (mSupportLineWidth[1] > 1) {
				mGL10.glDisable(GL10.GL_LINE_SMOOTH);// the platform no support smooth draw line while set the width of line.
				
				if (mSupportLineWidth[1] >= lineWidth)
					mGL10.glLineWidth(lineWidth);
				else
					mGL10.glLineWidth(mSupportLineWidth[1]);
			}
			else {
				mGL10.glLineWidth(1);
			}
		}
		else {
			mGL10.glLineWidth(1);
		}
	}
}
