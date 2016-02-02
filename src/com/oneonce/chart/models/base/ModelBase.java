package com.oneonce.chart.models.base;



/**
 * @author lianghua
 * @date 2015.01.08
 * @version 1.0
 */
public class ModelBase {

	protected EdgeInsets mMargin;// 除去外边（左，上，右，下）后绘制图形的区域


	public EdgeInsets getMargin() {
		return mMargin;
	}

	public void setMargin(EdgeInsets margin) {
		this.mMargin = margin;
	}
}
