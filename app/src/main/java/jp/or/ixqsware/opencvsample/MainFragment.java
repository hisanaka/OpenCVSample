package jp.or.ixqsware.opencvsample;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static jp.or.ixqsware.opencvsample.Constants.*;

public class MainFragment extends Fragment implements View.OnClickListener {
    private ImageView topFrame;
    private ImageView bottomFrame;
    private TextView topHash;
    private TextView bottomHash;
    private TextView distanceView;

    static{
        System.loadLibrary("opencv_java");
    }

    public static MainFragment newInstance(int sectionNumber) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        topFrame = (ImageView) rootView.findViewById(R.id.top_frame);
        bottomFrame = (ImageView) rootView.findViewById(R.id.bottom_frame);

        Button topButton = (Button) rootView.findViewById(R.id.draw_top_button);
        Button bottomButton = (Button) rootView.findViewById(R.id.draw_bottom_button);
        topButton.setOnClickListener(this);
        bottomButton.setOnClickListener(this);

        topHash = (TextView) rootView.findViewById(R.id.top_hash);
        bottomHash = (TextView) rootView.findViewById(R.id.bottom_hash);
        distanceView = (TextView) rootView.findViewById(R.id.distance_view);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        final int randNum = 64;
        Mat matPoints = new Mat(randNum, 2, CvType.CV_32S);
        ArrayList<Point> points = new ArrayList<>();
        int[] position = new int[2];

        int id = v.getId();
        switch (id) {
            case R.id.draw_top_button:
                Core.randu(matPoints, 100, 400);
                for (int i = 0; i < randNum; i++) {
                    matPoints.get(i, 0, position);
                    points.add(new Point(position[0], position[1]));
                }
                ImageGen topGen = new ImageGen(points);
                Bitmap bmpTop = topGen.getBitmap();
                String hashTop = topGen.getHash();
                topHash.setText(hashTop);
                topFrame.setImageBitmap(bmpTop);
                break;

            case R.id.draw_bottom_button:
                Core.randu(matPoints, 100, 400);
                for (int i = 0; i < randNum; i++) {
                    matPoints.get(i, 0, position);
                    points.add(new Point(position[0], position[1]));
                }
                ImageGen bottomGen = new ImageGen(points);
                Bitmap bmpBottom = bottomGen.getBitmap();
                String hashBottom = bottomGen.getHash();
                bottomHash.setText(hashBottom);
                bottomFrame.setImageBitmap(bmpBottom);

                if (topHash.getText().length() > 0) {
                    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
                    int distance = levenshteinDistance.getDistance(
                            topHash.getText().toString(), hashBottom);
                    distanceView.setText("Distance: " + distance);
                }
                break;
        }
    }
}
