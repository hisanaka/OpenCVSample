package jp.or.ixqsware.opencvsample.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * 手書きビュー
 */
public class DrawingView extends View {
    private Context context;
    private Canvas mCanvas;
    private Paint mPaint = new Paint(Paint.DITHER_FLAG);
    private Path mPath;
    private float mX;
    private float mY;
    private Bitmap bmp;
    private final int TOUCH_TOLERANCE = 4;

    public DrawingView(Context context) {
        super(context);
        this.context = context;
        mPath = new Path();
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (bmp == null) { bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444); }
        mCanvas = new Canvas(bmp);
        mCanvas.drawColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (bmp == null) {
            bmp = Bitmap.createBitmap(
                    this.getMeasuredWidth(), this.getMeasuredHeight(), Bitmap.Config.ARGB_4444);
        }
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawBitmap(bmp, 0, 0, mPaint);
        if (mPaint.getColor() != Color.TRANSPARENT) canvas.drawPath(mPath, mPaint);
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        setMeasuredDimension(size.x, size.y);
    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mPaint);
        mPath.reset();
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                if (mPaint.getColor() == Color.TRANSPARENT) {
                    mCanvas.drawPath(mPath, mPaint);
                }
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                if (mPaint.getColor() != Color.TRANSPARENT) {
                    touch_up();
                } else {
                    touch_move(x, y);
                    mCanvas.drawPath(mPath, mPaint);
                    invalidate();
                    touch_up();
                }
                invalidate();
                break;
        }
        return true;
    }

    public void clearCanvas() {
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        this.invalidate();
    }
}
