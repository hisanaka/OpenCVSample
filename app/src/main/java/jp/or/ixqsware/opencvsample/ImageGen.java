package jp.or.ixqsware.opencvsample;

import android.graphics.Bitmap;

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

/**
 * Created by hnakadate on 15/04/07.
 */
public class ImageGen {
    private Bitmap bmp;
    private String hash;

    static{
        System.loadLibrary("opencv_java");
    }

    public ImageGen(ArrayList<Point> points) {
        /* 新規画像生成 */
        Size sizeImage = new Size(500, 500);
        Mat matImage = Mat.zeros(sizeImage, CvType.CV_8UC3);

        /* ランダムに点を生成し、線で結ぶ */
        for (int i = 1; i < points.size(); i++) {
            Core.line(
                    matImage,
                    points.get(i - 1),
                    points.get(i),
                    new Scalar(255, 255, 255),
                    10,
                    Core.LINE_AA
            );
        }

        /* 外接矩形で切り抜き */
        MatOfPoint mapPoints = new MatOfPoint();
        mapPoints.fromList(points);
        Rect rect = Imgproc.boundingRect(mapPoints);
        Mat matTrim = new Mat();
        matImage.submat(rect).copyTo(matTrim);

        /* グレースケールに変更 */
        Imgproc.cvtColor(matTrim, matTrim, Imgproc.COLOR_RGB2GRAY);
        Imgproc.cvtColor(matTrim, matTrim, Imgproc.COLOR_GRAY2RGBA, 4);

        /* リサイズ(16 * 16)してHash値を計算(averageHash) */
        Imgproc.cvtColor(matTrim, matTrim, Imgproc.COLOR_RGB2HSV);
        Mat matResized = new Mat();
        Imgproc.resize(matTrim, matResized, new Size(16, 16), 0, 0, Imgproc.INTER_AREA);
        double[] buff = new double[16 * 16];
        double sum = 0;
        for (int r = 0; r < matResized.rows(); r++) {
            for (int c = 0; c < matResized.cols(); c++) {
                buff[r * 16 + c] = matResized.get(r, c)[2];
                sum += matResized.get(r, c)[2];
            }
        }
        double avg = sum / (16 * 16);  // 平均値
        StringBuilder sb = new StringBuilder();
        for (double d : buff) {
            sb.append(d > avg ? 1 : 0);
        }

        this.hash = sb.toString();
        this.bmp = matToBitmap(matTrim);
    }

    public Bitmap getBitmap() {
        return this.bmp;
    }

    public String getHash() {
        return this.hash;
    }

    private Bitmap matToBitmap(Mat src) {
        Mat dst = new Mat();
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGRA2RGBA, 4);
        Bitmap bmp = Bitmap.createBitmap(src.width(), src.height(), Bitmap.Config.ARGB_4444);
        Utils.matToBitmap(dst, bmp);
        return bmp;
    }
}
