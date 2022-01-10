package com.advc.pendroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;

public class CanvasView extends View {

    private Paint paint;
    private Path path;
    private int color;
    private int thickness;
    private ArrayList<PaintPath> paths;
    private ArrayList<PaintPath> undonePaths;
    private boolean startMoving;
    private boolean eraserMode;
    private boolean filledMode;
    private boolean moveMode;

    private int mode;
    public static final int DRAW = 0;
    public static final int DRAG = 1;
    public static final int ZOOM = 2;

    private boolean dragged;
    private boolean saveImage;

    private ScaleGestureDetector scaleDetector;
    private float scaleFactor = 1.f;

    private float canvasX;
    private float canvasY;
    private float translateX;
    private float translateY;
    private float previousTranslateX;
    private float previousTranslateY;
    private float lastTouchX;
    private float lastTouchY;
    private float startX;
    private float startY;

    private int pencilMode;
    private static final int PENCIL = 0;
    private static final int LINE = 1;
    private static final int RECTANGLE = 2;
    private static final int CIRCLE = 3;

    private static final int backgroundColor = Color.WHITE;

    public CanvasView(Context context) {

        super(context);

        pencilMode = PENCIL;

        paint = new Paint();
        path = new Path();
        paths = new ArrayList<PaintPath>();
        undonePaths = new ArrayList<PaintPath>();
        startMoving = false;
        eraserMode = false;
        filledMode = false;
        moveMode = false;

        mode = DRAW;

        dragged = false;
        saveImage = false;

        canvasX = 0f;
        canvasY = 0f;
        translateX = 0f;
        translateY = 0f;
        previousTranslateX = 0f;
        previousTranslateY = 0f;

        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        color = Color.BLACK;
        thickness = 10;

        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(thickness);
        paint.setColor(color);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        canvas.save();

        /*
            When the user touch the button to save the draw, then scale the canvas to the original size
            in order to save the complete canvas instead of the zoomed canvas
         */
        if (saveImage) canvas.scale(1f, 1f);
        else canvas.scale(scaleFactor, scaleFactor);

        if((translateX * -1) < 0) {
            translateX = 0;
        } else if((translateX * -1) > (scaleFactor - 1) * canvas.getWidth()) {
            translateX = (1 - scaleFactor) * canvas.getWidth();
        }

        if(translateY * -1 < 0) {
            translateY = 0;
        } else if((translateY * -1) > (scaleFactor - 1) * canvas.getHeight()) {
            translateY = (1 - scaleFactor) * canvas.getHeight();
        }

        if (saveImage) canvas.translate(0, 0);
        else canvas.translate(translateX / scaleFactor, translateY / scaleFactor);

        canvasX = canvas.getClipBounds().left;
        canvasY = canvas.getClipBounds().top;

        canvas.drawColor(backgroundColor);

        for(PaintPath pp : paths) {

            Paint currentPaint = pp.getPaint();
            currentPaint.setColor(pp.getColor());
            currentPaint.setStrokeWidth(pp.getThickness());

            canvas.drawPath(pp.getPath(), currentPaint);

        }

        canvas.restore();

        saveImage = false;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                lastTouchX = x - previousTranslateX;
                lastTouchY = y - previousTranslateY;

                startX = x / scaleFactor + canvasX;
                startY = y / scaleFactor + canvasY;

                if(moveMode) mode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if(mode == DRAG) {
                    mode = ZOOM;
                    Log.i("ACTION_DOWN", "Two Pointers Down");
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if(mode == DRAW) {

                    if(!eraserMode && !moveMode) draw(x / scaleFactor + canvasX, y / scaleFactor + canvasY);
                    else if(eraserMode && !moveMode) erase(x / scaleFactor + canvasX, y / scaleFactor + canvasY);

                } else if(moveMode) {

                    translateX = x - lastTouchX;
                    translateY = y - lastTouchY;

                    invalidate();

                    Log.i("CANVAS D", "W: " + getWidth() + ", H: " + getHeight());

                    if(mode == ZOOM) {
                        double distance = Math.sqrt(Math.pow(event.getX() - (lastTouchX + previousTranslateX), 2) +
                                Math.pow(event.getY() - (lastTouchY + previousTranslateY), 2)
                        );

                        if(distance > 0) {
                            dragged = true;
                            Log.i("CANVAS XY", "X: " + canvasX + ", Y: " + canvasY);
                        }
                    }

                }
                break;

            case MotionEvent.ACTION_UP:
                startMoving = false;
                mode = DRAW;
                dragged = false;

                previousTranslateX = translateX;
                previousTranslateY = translateY;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if(moveMode) mode = DRAG;

                previousTranslateX = translateX;
                previousTranslateY = translateY;
                break;

        }

        if(mode != DRAW) scaleDetector.onTouchEvent(event);

        if(mode == DRAW) invalidate();
        else if( (mode == DRAG && scaleFactor != 1f && dragged) || mode == ZOOM ) invalidate();

        return true;

    }

    public void setColor(int color) {

        this.color = color;
        paint.setColor(color);
        invalidate();

    }

    public int getColor() {
        return this.color;
    }

    public void setThickness(int thickness) {

        this.thickness = thickness;
        paint.setStrokeWidth(thickness);

    }

    public int getThickness() {
        return thickness;
    }

    public void setPencilMode(int pencilMode) {
        this.pencilMode = pencilMode;
    }

    public int getPencilMode() {
        return pencilMode;
    }

    public void setEraserMode(boolean eraserMode) {
        this.eraserMode = eraserMode;
    }

    public boolean getEraserMode() {
        return eraserMode;
    }

    public void setFilledMode(boolean filledMode) {
        this.filledMode = filledMode;
    }

    public boolean getFilledMode() {
        return filledMode;
    }

    public void setMoveMode(boolean moveMode) {
        this.moveMode = moveMode;
    }

    public boolean getMoveMode() {
        return moveMode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setSaveImage(boolean saveImage) { this.saveImage = saveImage; }

    public void undo() {

        if(!paths.isEmpty()) {

            PaintPath removedItem = paths.remove( paths.size() - 1 );
            undonePaths.add(removedItem);
            invalidate();

        }

    }

    public void redo() {

        if(!undonePaths.isEmpty()) {

            PaintPath removedItem = undonePaths.remove( undonePaths.size() - 1 );
            paths.add(removedItem);
            invalidate();

        }

    }

    // Draw when touch
    public void draw(float x, float y) {

        if(startMoving) {

            switch (pencilMode) {

                case PENCIL:
                    path.lineTo(x, y);
                    break;

                case LINE:
                    path.reset();
                    path.moveTo(startX, startY);
                    path.lineTo(x, y);
                    break;

                case RECTANGLE:
                    path.reset();
                    if(x < startX && y < startY) {
                        path.addRect(x, y, startX, startY, Path.Direction.CW);
                    } else if(y < startY) {
                        path.addRect(startX, y, x, startY, Path.Direction.CCW);
                    } else if(x < startX) {
                        path.addRect(x, startY, startX, y, Path.Direction.CCW);
                    } else {
                        path.addRect(startX, startY, x, y, Path.Direction.CW);
                    }
                    break;

                case CIRCLE:
                    path.reset();
                    if(x < startX && y < startY) {
                        path.addOval(x, y, startX, startY, Path.Direction.CW);
                    } else if(y < startY) {
                        path.addOval(startX, y, x, startY, Path.Direction.CCW);
                    } else if(x < startX) {
                        path.addOval(x, startY, startX, y, Path.Direction.CCW);
                    } else {
                        path.addOval(startX, startY, x, y, Path.Direction.CW);
                    }
                    break;

            }

            invalidate();

        } else {

            // Create a new Path and set startMoving to true

            path = new Path();

            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);

            if(filledMode && (pencilMode == RECTANGLE || pencilMode == CIRCLE)) paint.setStyle(Paint.Style.FILL);
            else paint.setStyle(Paint.Style.STROKE);

            paint.setStrokeWidth(thickness);
            paint.setColor(color);

            PaintPath paintPath = new PaintPath(color, thickness, path, paint);
            paths.add(paintPath);

            path.reset();
            path.moveTo(x, y);

            startMoving = true;

        }

    }

    public void erase(float x, float y) {

        if(startMoving) {

            path.lineTo(x, y);

        } else {

            path = new Path();

            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(thickness);
            paint.setColor(backgroundColor);

            PaintPath paintPath = new PaintPath(backgroundColor, thickness, path, paint);
            paths.add(paintPath);

            path.reset();
            path.moveTo(x, y);
            startMoving = true;

        }

    }

    public void clearCanvas() {

        paths.clear();
        undonePaths.clear();
        invalidate();

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(1f, Math.min(scaleFactor, 5.0f));

            invalidate();
            return true;

        }

    }

}
