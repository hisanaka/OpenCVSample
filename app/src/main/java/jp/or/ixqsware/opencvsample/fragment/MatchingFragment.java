package jp.or.ixqsware.opencvsample.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.or.ixqsware.opencvsample.GestureInformation;
import jp.or.ixqsware.opencvsample.ImageGen;
import jp.or.ixqsware.opencvsample.MainActivity;
import jp.or.ixqsware.opencvsample.R;
import jp.or.ixqsware.opencvsample.db.RingDatabaseHelper;
import jp.or.ixqsware.opencvsample.view.DrawingView;

import static jp.or.ixqsware.opencvsample.Constants.ARG_SECTION_NUMBER;
import static jp.or.ixqsware.opencvsample.Constants.DRAWING_SECTION_ID;

/**
 * Created by hnakadate on 15/04/22.
 */
public class MatchingFragment extends Fragment implements View.OnClickListener {
    private FrameLayout drawingFrame;
    private ListView listView;
    private SQLiteDatabase db;
    private GestureListAdapter adapter;

    public static MatchingFragment newInstance(int sectionNumber) {
        MatchingFragment fragment = new MatchingFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_matching, container, false);

        drawingFrame = (FrameLayout) rootView.findViewById(R.id.drawing_view);
        DrawingView drawingView = new DrawingView(getActivity());
        drawingFrame.addView(drawingView);

        ImageView eraseButton = (ImageView) rootView.findViewById(R.id.erase_button);
        eraseButton.setOnClickListener(this);

        Button matchingButton = (Button) rootView.findViewById(R.id.matching_button);
        matchingButton.setOnClickListener(this);

        listView = (ListView) rootView.findViewById(R.id.matching_list);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        DrawingView drawingView = (DrawingView) drawingFrame.getChildAt(0);

        switch (id) {
            case R.id.erase_button:
                drawingView.clearCanvas();
                break;

            case R.id.matching_button:
                ArrayList<Point> arrPoints = drawingView.getPoints();
                if (arrPoints.size() == 0) { return; }

                ImageGen imageGen = new ImageGen(arrPoints, drawingView.getWidth(), drawingView.getHeight());
                String mHash = imageGen.getHash();

                RingDatabaseHelper helper = RingDatabaseHelper.getInstance(getActivity());
                if (db == null || !db.isOpen()) db = helper.getReadableDatabase();
                // TODO 比較
                ArrayList<GestureInformation> arrGestures
                        = helper.getAllGestures(db, mHash, drawingView.getWidth(), drawingView.getHeight());
                adapter = new GestureListAdapter(getActivity(), 0, arrGestures);
                listView.setAdapter(adapter);
                adapter.setNotifyOnChange(true);
                break;
        }
    }

    static class ViewHolder {
        ImageView gestureView;
        TextView idView;
        TextView distanceView;
    }

    public class GestureListAdapter extends ArrayAdapter<GestureInformation> {
        private LayoutInflater layoutInflater;
        private RingDatabaseHelper helper;
        private SQLiteDatabase db = null;

        public GestureListAdapter(Context context, int textViewResourceId, List<GestureInformation> objects) {
            super(context, textViewResourceId, objects);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            helper = RingDatabaseHelper.getInstance(getActivity());
            if (db == null || !db.isOpen()) db = helper.getReadableDatabase();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final GestureInformation item = (GestureInformation) getItem(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.result_item, null);
                viewHolder = new ViewHolder();
                viewHolder.gestureView = (ImageView) convertView.findViewById(R.id.gesture_image);
                viewHolder.idView = (TextView) convertView.findViewById(R.id.id_view);
                viewHolder.distanceView = (TextView) convertView.findViewById(R.id.distance_view);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.gestureView.setImageBitmap(item.getBitmap());
            viewHolder.idView.setText(Long.toString(item.getId()));
            viewHolder.distanceView.setText(item.getDifferenceRate() + "%");

            return convertView;
        }
    }
}
