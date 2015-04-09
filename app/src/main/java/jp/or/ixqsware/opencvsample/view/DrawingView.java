package jp.or.ixqsware.opencvsample.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

import jp.or.ixqsware.opencvsample.R;

/**
 * 手書きビュー
 */
public class DrawingView extends View {
    private Canvas mCanvas;
    private Bitmap bmp;
    private boolean flgNew = true;
    private Paint mPaint = new Paint(Paint.DITHER_FLAG);
    private ArrayList<Point> arrPoints = new ArrayList<>();

    public DrawingView(Context context) {
        super(context, null);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(15);
        mPaint.setColor(getResources().getColor(R.color.paint_color));

        Point nP = new Point(-1, -1);
        for (Point mP : arrPoints) {
            if (mP.x >= 0) {
                if (nP.x < 0) { nP = mP; }
                canvas.drawLine(nP.x, nP.y, mP.x, mP.y, mPaint);
            }
            nP = mP;
        }
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        setMeasuredDimension(size.x, size.y);
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN && flgNew) {
            clearCanvas();
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        arrPoints.add(new Point(x, y));

        if (action == MotionEvent.ACTION_UP) {
            arrPoints.add(new Point(-1, -1));
            flgNew = true;
        }
        invalidate();
        return true;
    }

    public void clearCanvas() {
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        arrPoints.clear();
        this.invalidate();
    }

    public ArrayList<Point> getPoints() {
        return this.arrPoints;
    }
}
