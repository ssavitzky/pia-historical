/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 10.4.97 - revised 02-06-98
 *
 */
package ricoh.rh;

public class RH_HighlightStyle {
    private int r=0, g=0, b=0, r2=0, g2=0, b2=0, bold=0, under=0, box=0, shadow=0, whole=0, idx=0;
    private String tip;
    
    public RH_HighlightStyle (int b, int u, int bx, int s, int w, int r_color, int g_color, 
			      int b_color, int r_color2, int g_color2, int b_color2, int i, String tooltip) {
	bold=b;
	under=u;
	shadow=s;
	whole=w;
	idx=i;
	r=r_color;
	g=g_color;
	b=b_color;
	r2=r_color2;
	g2=g_color2;
	b2=b_color2;
	tip=tooltip;
    }
    public int getBold() {
	return bold;
    }
    public void setBold(int n) {
	bold=n;
    }
    public int getUnder() {
	return under;
    }
    public void setUnder(int n) {
	under=n;
    }
    public int getBox() {
	return box;
    }
    public void setBox(int n) {
	box=n;
    }
    public int getShadow() {
	return shadow;
    }
    public void setShadow(int n) {
	shadow=n;
    }
    public int getWhole() {
	return whole;
    }
    public void setWhole(int n) {
	whole=n;
    }
    public int getRed() {
	return r;
    }
    public void setRed(int n) {
	r=n;
    }
    public int getGreen() {
	return g;
    }
    public void setGreen(int n) {
	g=n;
    }
    public int getBlue() {
	return b;
    }
    public void setBlue(int n) {
	b=n;
    }
    public int getForeRed() {
	return r2;
    }
    public void setForeRed(int n) {
	r2=n;
    }
    public int getForeGreen() {
	return g2;
    }
    public void setForeGreen(int n) {
	g2=n;
    }
    public int getForeBlue() {
	return b2;
    }
    public void setForeBlue(int n) {
	b2=n;
    }
    public String getTip() {
	return tip;
    }
    public void setTip(String n) {
	tip=n;
    }
    /**
     * Returns the index value of this style in the array of all styles, e.g. highlightStyle[idx]
     */
    public int getIdx() {
	return idx;
    }
    public void setIdx(int n) {
	idx=n;
    }
}
