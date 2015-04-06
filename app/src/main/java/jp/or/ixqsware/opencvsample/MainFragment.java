package jp.or.ixqsware.opencvsample;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import static jp.or.ixqsware.opencvsample.Constants.*;

public class MainFragment extends Fragment implements View.OnClickListener {
    private FrameLayout bottomFrame;

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

        FrameLayout topFrame = (FrameLayout) rootView.findViewById(R.id.top_frame);
        bottomFrame = (FrameLayout) rootView.findViewById(R.id.bottom_frame);

        Button drawButton = (Button) rootView.findViewById(R.id.draw_button);
        drawButton.setOnClickListener(this);

        DrawingView drawingView = new DrawingView(getActivity(), null);
        topFrame.addView(drawingView);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.draw_button:
                bottomFrame.removeAllViews();
                DrawingView drawingView = new DrawingView(getActivity(), null);
                bottomFrame.addView(drawingView);
                break;
        }
    }
}
