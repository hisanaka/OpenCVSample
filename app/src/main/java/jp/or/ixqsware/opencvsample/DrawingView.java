package jp.or.ixqsware.opencvsample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {
    private Paint paint = new Paint();

    static{
        System.loadLibrary("opencv_java");
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Size sizeImage = new Size(500, 500);
        Mat matImage = Mat.zeros(sizeImage, CvType.CV_8UC3);

        final int randNum = 5;
        Mat matPoints = new Mat(randNum, 2, CvType.CV_32S);

        /* ランダムに点を描画 */
        Core.randu(matPoints, 100, 400);
        List<Point> points = new ArrayList<>();
        int[] position = new int[2];
        for (int i = 0; i < randNum; i++) {
            matPoints.get(i, 0, position);
            points.add(new Point(position[0], position[1]));
            Core.circle(matImage, points.get(i), 2, new Scalar(200, 200, 0), -1);
            if (i > 0) {
                Core.line(matImage, points.get(i - 1), points.get(i), new Scalar(0, 0, 255));
            }
        }

        /* 外接矩形を求める */
        MatOfPoint mapPoints = new MatOfPoint();
        mapPoints.fromList(points);
        Rect rect = Imgproc.boundingRect(mapPoints);
        Core.rectangle(matImage, rect.tl(), rect.br(), new Scalar(100, 100, 200), 2);

        Bitmap img = matToBitmap(matImage);
        canvas.drawBitmap(img, 0, 0, paint);
    }

    private Bitmap matToBitmap(Mat src) {
        Mat dst = new Mat();
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGRA2RGBA, 4);
        Bitmap bmp = Bitmap.createBitmap(src.width(), src.height(), Bitmap.Config.ARGB_4444);
        Utils.matToBitmap(dst, bmp);
        return bmp;
    }
}
