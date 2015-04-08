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

import static jp.or.ixqsware.opencvsample.Constants.*;

public class ImageGen {
    private Bitmap bmp;
    private Bitmap originBmp;
    private String hash;

    static{
        System.loadLibrary("opencv_java");
    }

    /**
     * コンストラクタ
     * 引数がない場合はランダムに点を描画した画像を作成
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
                        new Scalar(255, 255, 255),
                        10,
                        Core.LINE_AA
                );
            }
        }
        this.originBmp = matToBitmap(mat);

        mat = trimImage(mat, points);
        mat = resizeMat(mat);
        this.hash = calculateHash(mat);
        this.bmp = matToBitmap(mat);
    }

    /**
     * コンストラクタ
     * Bitmap画像を指定
     */
    public ImageGen(Bitmap src) {
        Mat mat = new Mat();
        Utils.bitmapToMat(src.copy(Bitmap.Config.ARGB_8888, true), mat);
        this.originBmp = matToBitmap(mat);

        mat = resizeMat(mat);
        this.hash = calculateHash(mat);
        this.bmp = matToBitmap(mat);
    }

    /**
     * 外接矩形で画像を切り抜く
     * @param src 元画像
     * @param points 点座標のリスト
     * @return 切り抜き後の画像
     */
    private Mat trimImage(Mat src, ArrayList<Point> points) {
        MatOfPoint mapPoints = new MatOfPoint();
        mapPoints.fromList(points);
        Rect rect = Imgproc.boundingRect(mapPoints);
        Mat dst = new Mat();
        src.submat(rect).copyTo(dst);
        return dst;
    }

    /**
     * グレースケールに変更のうえ、画像サイズを16x16に変更する
     * @param src 元画像
     * @return 変更後の画像
     */
    private Mat resizeMat(Mat src) {
        Mat dst = new Mat();
        src.copyTo(dst);
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_RGB2GRAY);
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_GRAY2RGBA, 4);
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_RGB2HSV);
        Imgproc.resize(dst, dst, new Size(IMG_WIDTH, IMG_HEIGHT), 0, 0, Imgproc.INTER_AREA);
        return dst;
    }

    /**
     * ピクセルごとの明度からハッシュ値(averageHash)を計算
     * @param src HSV形式の画像
     */
    private String calculateHash(Mat src) {
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
        return sb.toString();
    }

    /**
     * Mat -> Bitmap変換
     * @param src 元画像(Mat)
     * @return Bitmap返還後の画像
     */
    private Bitmap matToBitmap(Mat src) {
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
}
