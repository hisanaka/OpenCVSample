package jp.or.ixqsware.opencvsample.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

import jp.or.ixqsware.opencvsample.ImageGen;
import jp.or.ixqsware.opencvsample.MainActivity;
import jp.or.ixqsware.opencvsample.R;
import jp.or.ixqsware.opencvsample.db.RingDatabaseHelper;
import jp.or.ixqsware.opencvsample.view.DrawingView;

import static jp.or.ixqsware.opencvsample.Constants.ARG_SECTION_NUMBER;
import static jp.or.ixqsware.opencvsample.Constants.DRAWING_SECTION_ID;

public class DrawingFragment extends Fragment implements View.OnClickListener {
    private FrameLayout drawingFrame;
    private SQLiteDatabase db;

    public static DrawingFragment newInstance(int sectionNumber) {
        DrawingFragment fragment = new DrawingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER, DRAWING_SECTION_ID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_drawing, container, false);

        drawingFrame = (FrameLayout) rootView.findViewById(R.id.drawing_view);

        Button registerButton = (Button) rootView.findViewById(R.id.register_button);
        Button cancelButton = (Button) rootView.findViewById(R.id.cancel_button);
        registerButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        DrawingView drawingView = new DrawingView(getActivity());

        drawingFrame.addView(drawingView);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        DrawingView drawingView = (DrawingView) drawingFrame.getChildAt(0);

        switch (id) {
            case R.id.cancel_button:
                drawingView.clearCanvas();
                break;

            case R.id.register_button:
                ArrayList<Point> arrPoints = drawingView.getPoints();
                if (arrPoints.size() == 0) { return; }

                ImageGen imageGen = new ImageGen(arrPoints, drawingView.getWidth(), drawingView.getHeight());
                StringBuilder sb = new StringBuilder();
                for (Point p : arrPoints) {
                    sb.append(p.x);
                    sb.append(":");
                    sb.append(p.y);
                    sb.append(":");
                }
                String strPoints = sb.toString();
                strPoints = strPoints.substring(0, strPoints.length() - 1);

                RingDatabaseHelper helper = RingDatabaseHelper.getInstance(getActivity());
                if (db == null || !db.isOpen()) db = helper.getReadableDatabase();
                helper.registerGesture(db, imageGen.getHash(), strPoints);

                break;
        }
    }
}
