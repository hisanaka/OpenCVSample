package jp.or.ixqsware.opencvsample.fragment;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import jp.or.ixqsware.opencvsample.ImageGen;
import jp.or.ixqsware.opencvsample.LevenshteinDistance;
import jp.or.ixqsware.opencvsample.R;
import jp.or.ixqsware.opencvsample.view.DrawingView;

import static jp.or.ixqsware.opencvsample.Constants.ARG_SECTION_NUMBER;

/**
 * Created by hnakadate on 15/04/09.
 */
public class DrawingFragment extends Fragment implements View.OnClickListener {
    private FrameLayout topFrame;
    private FrameLayout bottomFrame;
    private TextView distanceView;

    public static DrawingFragment newInstance(int sectionNumber) {
        DrawingFragment fragment = new DrawingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_drawing, container, false);

        topFrame = (FrameLayout) rootView.findViewById(R.id.top_drawing_view);
        bottomFrame = (FrameLayout) rootView.findViewById(R.id.bottom_drawing_view);

        Button topEraseButton = (Button) rootView.findViewById(R.id.top_erase_button);
        Button bottomEraseButton = (Button) rootView.findViewById(R.id.bottom_erase_button);
        Button calculateButton = (Button) rootView.findViewById(R.id.calculate_button);
        topEraseButton.setOnClickListener(this);
        bottomEraseButton.setOnClickListener(this);
        calculateButton.setOnClickListener(this);

        distanceView = (TextView) rootView.findViewById(R.id.distance_view);

        DrawingView topDrawing = new DrawingView(getActivity());
        DrawingView bottomDrawing = new DrawingView(getActivity());

        topFrame.addView(topDrawing);
        bottomFrame.addView(bottomDrawing);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        DrawingView topDraw = (DrawingView) topFrame.getChildAt(0);
        DrawingView bottomDraw = (DrawingView) bottomFrame.getChildAt(0);

        switch (id) {
            case R.id.top_erase_button:
                topDraw.clearCanvas();
                break;
            case R.id.bottom_erase_button:
                bottomDraw.clearCanvas();
                break;
            case R.id.calculate_button:
                ArrayList<Point> arrTop = topDraw.getPoints();
                ArrayList<Point> arrBottom = bottomDraw.getPoints();
                if (arrTop.size() == 0 || arrBottom.size() == 0) { return; }

                ImageGen topGen = new ImageGen(arrTop, topFrame.getWidth(), topFrame.getHeight());
                ImageGen bottomGen = new ImageGen(arrBottom, bottomFrame.getWidth(), bottomDraw.getWidth());
                String topHash = topGen.getHash();
                String bottomHash = bottomGen.getHash();

                /*
                Bitmap bmpTop = topGen.getOriginalBitmap();
                saveImage(bmpTop, "top");
                Bitmap bmpBottom = bottomGen.getOriginalBitmap();
                saveImage(bmpBottom, "bottom");
                 */

                LevenshteinDistance lDistance = new LevenshteinDistance();
                int distance = lDistance.calculateDistance(topHash, bottomHash);
                double ratio = (double) distance * 100 / (double) topHash.length();
                distanceView.setText(getString(R.string.distance_label, distance, ratio));
                break;
        }
    }

    private void saveImage(Bitmap bmp, String fileName) {
        String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String savePath = storagePath + "/Download/OpenCV/";
        File saveDir = new File(savePath);
        if (!saveDir.exists()) { saveDir.mkdir(); }

        String saveName = savePath + fileName + ".png";
        try {
            FileOutputStream fos = new FileOutputStream(saveName);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
