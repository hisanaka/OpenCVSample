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
    private Bitmap bmp;
    private Bitmap originBmp;
    private String hash;
    private Mat matFeature;

    static{
        System.loadLibrary("opencv_java");
    }

    /**
     * コンストラクタ(引数がない場合はランダムに点を描画した画像を作成)
     */
    public ImageGen() {
        Size sizeImage = new Size(500, 500);
        Mat mat = Mat.zeros(sizeImage, CvType.CV_8UC3);

        /* ランダムに点を描画 */
        final int randNum = 16;
        Mat matPoints = new Mat(randNum, 2, CvType.CV_32S);
        Core.randu(matPoints, 100, 400);
        ArrayList<Point> points = new ArrayList<>();
        int[] position = new int[2];
        for (int i = 0; i < randNum; i++) {
            matPoints.get(i, 0, position);
            points.add(new Point(position[0], position[1]));
            if (i > 0) {
                Core.line(
                        mat,
                        points.get(i - 1),
                        points.get(i),
                        new Scalar(
                                (130 * i + 255 * (randNum - i)) / randNum,
                                0,
                                (255 * i + 130 * (randNum - i)) / randNum),
                        5
                );
            }
        }
        this.originBmp = matToBitmap(mat);

        mat = trimImage(mat, points);
        mat = resizeImage(mat, IMG_WIDTH, IMG_HEIGHT);
        this.hash = calculateHash(mat);
        this.bmp = matToBitmap(mat);
    }

    /**
     * コンストラクタ(Bitmap画像が指定された場合)
     */
    public ImageGen(Bitmap src) {
        Mat mat = new Mat();
        Utils.bitmapToMat(src.copy(Bitmap.Config.ARGB_8888, false), mat);
        mat = convertGrayScale(mat);
        this.originBmp = matToBitmap(mat);

        mat = resizeImage(mat, IMG_WIDTH, IMG_HEIGHT);
        Mat brightness = getBrightnessGraph(mat);
        this.bmp = matToBitmap(brightness);
        this.hash = calculateHash(resizeImage(brightness, IMG_WIDTH, IMG_HEIGHT));

        /*
        this.matFeature = getFeaturePoint(mat);

        mat = resizeImage(mat, IMG_WIDTH, IMG_HEIGHT);
        this.hash = calculateHash(mat);
        this.bmp = matToBitmap(mat);
        */
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
        // TODO
        mat = trimImage(mat, points);
        this.originBmp = matToBitmap(convertGrayScale(mat));

        mat = resizeImage(mat, IMG_WIDTH, IMG_HEIGHT);
        Mat brightness = getBrightnessGraph(mat);
        this.bmp = matToBitmap(brightness);
        this.hash = calculateHash(resizeImage(brightness, IMG_WIDTH, IMG_HEIGHT));
        /*
        mat = resizeImage(mat, IMG_WIDTH, IMG_HEIGHT);

        Mat tmp = new Mat();
        mat.copyTo(tmp);
        tmp = convertGrayScale(tmp);
        this.matFeature = getFeaturePoint(tmp);

        this.hash = calculateHash(mat);
        this.bmp = matToBitmap(mat);
         */
    }

    /**
     * TODO 特徴点抽出
     * @param src
     * @return
     */
    private Mat getFeaturePoint(final Mat src) {
        Mat tmp = new Mat();
        Imgproc.cvtColor(src, tmp, Imgproc.COLOR_RGB2GRAY);

        Mat dst = new Mat();
        MatOfKeyPoint keyPoint = new MatOfKeyPoint();
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
        detector.detect(tmp, keyPoint);
        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        extractor.compute(tmp, keyPoint, dst);
        Log.d("DEBUG:", "Dst:" + dst.empty());
        return dst;
    }

    /**
     * TODO 特徴点による画像比較
     * @param mat1
     * @param mat2
     * @return
     */
    public double comapreImage(Mat mat1, Mat mat2) {
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        MatOfDMatch matOfDMatch = new MatOfDMatch();
        matcher.match(mat1, mat2, matOfDMatch);
        DMatch[] dMatches = matOfDMatch.toArray();
        Log.d("DEBUG:", "DMatch:" + dMatches.length);
        int cnt = 0;
        for (int i = 0; i < dMatches.length; i++) {
            if (dMatches[i].distance < 50) {
                cnt++;
            }
        }
        return (double) cnt * 100 / dMatches.length;
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
        Mat grayScale = convertGrayScale(src);
        Mat dst = new Mat(new Size(src.rows() * src.cols(), 256), CvType.CV_8UC3);
        for (int r = 0; r < grayScale.rows(); r++) {
            for (int c = 0; c < grayScale.cols(); c++) {
                Core.line(
                        dst,
                        new Point((r * 16) + c, 0),
                        new Point((r * 16) + c, grayScale.get(r, c)[2]),
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
        double[] buff = new double[IMG_WIDTH * IMG_HEIGHT];
        double sum = 0;
        for (int r = 0; r < src.rows(); r++) {
            for (int c = 0; c < src.cols(); c++) {
                buff[r * IMG_HEIGHT + c] = src.get(r, c)[2];
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
     * Mat -> Bitmap変換
     * @param src 元画像(Mat)
     * @return Bitma変換後の画像
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

    public Bitmap getOriginalBitmap() { return this.originBmp; }

    public String getHash() {
        return this.hash;
    }

    public Mat getFeatureMat() { return this.matFeature; }
}
