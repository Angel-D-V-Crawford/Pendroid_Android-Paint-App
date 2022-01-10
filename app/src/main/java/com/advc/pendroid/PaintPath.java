package com.advc.pendroid;

import android.graphics.Paint;
import android.graphics.Path;

public class PaintPath {

    private int color;
    private int thickness;
    private Path path;
    private Paint paint;

    public PaintPath(int color, int thickness, Path path) {

        this.color = color;
        this.thickness = thickness;
        this.path = path;
//        paint = new Paint();
//        paint.setColor(color);
//        paint.setStrokeWidth(thickness);
//        paint.setStyle(Paint.Style.FILL);

    }

    public PaintPath(int color, int thickness, Path path, Paint paint) {

        this.color = color;
        this.thickness = thickness;
        this.path = path;
        this.paint = paint;

    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Paint getPaint() { return paint; }

    public void setPaint(Paint paint) { this.paint = paint; }
}
