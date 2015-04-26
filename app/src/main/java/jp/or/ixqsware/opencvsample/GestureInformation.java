package jp.or.ixqsware.opencvsample;

import android.graphics.Bitmap;
import android.graphics.Point;
import java.util.ArrayList;

/**
 * Created by hnakadate on 15/04/16.
 */
public class GestureInformation {
    private long id;
    private Bitmap bmp;
    private float difference;
    private String hash;
    private String points;

    public GestureInformation() {
        this.id = -1L;
        this.bmp = null;
        this.hash = "";
        this.difference = 0.0f;
        this.points = "";
    }

    public long getId() {
        return id;
    }

    public Bitmap getBitmap() {
        return bmp;
    }

    public float getDifferenceRate() {
        return difference;
    }

    String getHash() { return this.hash; }

    String getPoints() { return this.points; }

    public void setId(long id_) {
        this.id = id_;
    }

    public void setDifferenceRate(float rate) {
        this.difference = rate;
    }

    public void setHash(String hash_) {
        this.hash = hash_;
    }

    public void setBitmap(String decString, int width, int height) {
        String[] lstDec = decString.split(":");
        ArrayList<Point> arrPoints = new ArrayList<>();
        for (int i = 0; i < lstDec.length; i += 2) {
            int x = Integer.parseInt(lstDec[i]);
            int y = Integer.parseInt(lstDec[i + 1]);
            Point point = new Point(x, y);
            arrPoints.add(point);
        }
        ImageGen imageGen = new ImageGen(arrPoints, width, height);
        this.bmp = imageGen.getBitmap();
    }

    public void setPoints(String points_) {
        this.points = points_;
    }
}
