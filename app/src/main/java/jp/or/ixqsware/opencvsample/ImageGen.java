package jp.or.ixqsware.opencvsample;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static jp.or.ixqsware.opencvsample.Constants.*;

public class ImageGen {
    private Bitmap bmp = null;
    private String hash = "";

    static{
        System.loadLibrary("opencv_java");
    }

    /**
     * コンストラクタ(点座標が指定された場合)
     * @param arrPoints 点座標
     * @param width 画像の幅
     * @param height 画像の高さ
     */
    public ImageGen(ArrayList<android.graphics.Point> arrPoints, int width, int height) {
        Mat mat = Mat.zeros(new Size(width, height), CvType.CV_8UC3);
        ArrayList<Point> points = new ArrayList<>();
        int arraySize = arrPoints.size();
        for (int i = 0; i < arrPoints.size(); i++) {
            android.graphics.Point p = arrPoints.get(i);
            if (p.x < 0 || p.y < 0) { continue; }
            points.add(new Point(p.x, p.y));
            if (i > 0) {
                Core.line(
                        mat,
                        points.get(i - 1),
                        points.get(i),
                        new Scalar(
                                (25 * i + 255 * (arraySize - i)) / arraySize,
                                (25 * i + 255 * (arraySize - i)) / arraySize,
                                (25 * i + 255 * (arraySize - i)) / arraySize),
                        10
                );
            }
        }
        mat = trimImage(mat, points);

        Mat matBitmap = new Mat();
        mat.copyTo(matBitmap);
        //this.bmp = matToBitmap(invertColor(matBitmap));
        this.bmp = matToBitmap(matBitmap);

        mat = convertGrayScale(mat);
        mat = resizeImage(mat, IMG_WIDTH * 4, IMG_WIDTH * 4);

        Mat brightness = getBrightnessGraph(mat);
        brightness = resizeImage(brightness, IMG_WIDTH, IMG_HEIGHT);
        this.hash = calculateHash(brightness);
    }

    /**
     * 外接矩形で画像を切り抜く
     * @param src 元画像
     * @param points 点座標のリスト
     * @return 切り抜き後の画像
     */
    private Mat trimImage(final Mat src, ArrayList<Point> points) {
        MatOfPoint mapPoints = new MatOfPoint();
        mapPoints.fromList(points);
        Rect rect = Imgproc.boundingRect(mapPoints);
        Mat dst = new Mat();
        src.submat(rect).copyTo(dst);
        return dst;
    }

    /**
     * ピクセル毎の明度をグラフ化
     * @param src 元画像
     * @return 縦軸に明度、横軸にピクセルをとったグラフ
     */
    private Mat getBrightnessGraph(final Mat src) {
        Mat tmp = new Mat();
        Imgproc.cvtColor(src, tmp, Imgproc.COLOR_RGB2HSV);

        Mat dst = new Mat(new Size(tmp.rows() * tmp.cols(), 256), CvType.CV_8UC3);
        for (int r = 0; r < tmp.rows(); r++) {
            for (int c = 0; c < tmp.cols(); c++) {
                Core.line(
                        dst,
                        new Point((r * tmp.cols()) + c, 0),
                        new Point((r * tmp.cols()) + c, tmp.get(r, c)[2]),
                        new Scalar(255, 255, 255),
                        1
                );
            }
        }
        return dst;
    }

    /**
     * グレースケールに変更する
     * @param src 元画像
     * @return グレースケールに変換された画像
     */
    private Mat convertGrayScale(final Mat src) {
        Mat dst = new Mat();
        src.copyTo(dst);
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_RGB2GRAY);
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_GRAY2RGBA, 4);
        return dst;
    }

    /**
     * 画像サイズを変更する
     * @param src 元画像
     * @param width 変更後の画像の幅
     * @param height 変更後の画像の高さ
     * @return サイズ変更後の画像
     */
    private Mat resizeImage(final Mat src, int width, int height) {
        Mat dst = new Mat();
        src.copyTo(dst);
        Imgproc.resize(dst, dst, new Size(width, height), 0, 0, Imgproc.INTER_AREA);
        return dst;
    }

    /**
     * ピクセルごとの明度からハッシュ値(averageHash)を計算
     * @param src 元画像
     */
    private String calculateHash(final Mat src) {
        String mHash = "";

        Mat dst = new Mat();
        src.copyTo(dst);
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_RGB2HSV);
        double[] buff = new double[dst.rows() * dst.cols()];
        double sum = 0;
        for (int r = 0; r < src.rows(); r++) {
            for (int c = 0; c < src.cols(); c++) {
                buff[r * dst.cols() + c] = src.get(r, c)[2];
                sum += src.get(r, c)[2];
            }
        }
        double avg = sum / buff.length;  // 濃淡の平均値

        StringBuilder sb = new StringBuilder();
        for (double d : buff) {
            sb.append(d > avg ? 1 : 0);
        }
        mHash = sb.toString();
        return mHash;
    }

    /**
     * 色反転
     * @param src 元画像
     * @return 色を反転した画像
     */
    private Mat invertColor(final Mat src) {
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2GRAY);
        Core.bitwise_not(src, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_GRAY2BGRA, 4);
        return src;
    }

    /**
     * Mat -> Bitmap変換
     * @param src 元画像(Mat)
     * @return Bitmap変換後の画像
     */
    private Bitmap matToBitmap(final Mat src) {
        Mat dst = new Mat();
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGRA2RGBA, 4);
        Bitmap bmp = Bitmap.createBitmap(src.width(), src.height(), Bitmap.Config.ARGB_4444);
        Utils.matToBitmap(dst, bmp);
        return bmp;
    }

    public Bitmap getBitmap() {
        return this.bmp;
    }

    public String getHash() {
        return this.hash;
    }
}
