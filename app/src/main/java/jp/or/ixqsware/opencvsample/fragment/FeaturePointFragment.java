package jp.or.ixqsware.opencvsample.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jp.or.ixqsware.opencvsample.ImageGen;
import jp.or.ixqsware.opencvsample.LevenshteinDistance;
import jp.or.ixqsware.opencvsample.MainActivity;
import jp.or.ixqsware.opencvsample.R;
import jp.or.ixqsware.opencvsample.view.DrawingView;

import static jp.or.ixqsware.opencvsample.Constants.*;

/**
 * Created by hnakadate on 15/04/12.
 */
public class FeaturePointFragment extends Fragment implements OnClickListener {
    private FrameLayout topFrame;
    private FrameLayout bottomFrame;
    private TextView distanceView;

    public static FeaturePointFragment newInstance(int sectionNumber) {
        FeaturePointFragment fragment = new FeaturePointFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER, FEATURE_POINT_SECTION_ID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_drawing, container, false);

        topFrame = (FrameLayout) rootView.findViewById(R.id.top_drawing_view);
        bottomFrame = (FrameLayout) rootView.findViewById(R.id.bottom_drawing_view);

        ImageView topEraseButton = (ImageView) rootView.findViewById(R.id.top_erase_button);
        ImageView bottomEraseButton = (ImageView) rootView.findViewById(R.id.bottom_erase_button);
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
                if (arrTop.size() == 0 || arrBottom.size() == 0) {
                    return;
                }

                ImageGen topGen = new ImageGen(arrTop, topFrame.getWidth(), topFrame.getHeight(), 0);
                ImageGen bottomGen = new ImageGen(arrBottom, bottomFrame.getWidth(), bottomFrame.getHeight(), 0);

                double distance = topGen.compareImage(topGen.getFeatureMat(), bottomGen.getFeatureMat());
                distanceView.setText("Match: " + distance + "%");
                break;
        }
    }
}
