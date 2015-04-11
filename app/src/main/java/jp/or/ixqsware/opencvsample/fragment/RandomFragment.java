package jp.or.ixqsware.opencvsample.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import jp.or.ixqsware.opencvsample.ImageGen;
import jp.or.ixqsware.opencvsample.LevenshteinDistance;
import jp.or.ixqsware.opencvsample.MainActivity;
import jp.or.ixqsware.opencvsample.R;

import static jp.or.ixqsware.opencvsample.Constants.ARG_SECTION_NUMBER;
import static jp.or.ixqsware.opencvsample.Constants.RANDOM_SECTION_ID;
import static jp.or.ixqsware.opencvsample.Constants.REQUEST_BOTTOM;
import static jp.or.ixqsware.opencvsample.Constants.REQUEST_TOP;

public class RandomFragment extends Fragment implements View.OnClickListener {
    private ImageView topImage;
    private ImageView bottomImage;
    private TextView topHashView;
    private TextView bottomHashView;
    private TextView distanceView;

    public static RandomFragment newInstance(int sectionNumber) {
        RandomFragment fragment = new RandomFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER, RANDOM_SECTION_ID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        topImage = (ImageView) rootView.findViewById(R.id.top_frame);
        bottomImage = (ImageView) rootView.findViewById(R.id.bottom_frame);

        topHashView = (TextView) rootView.findViewById(R.id.top_hash_view);
        bottomHashView = (TextView) rootView.findViewById(R.id.bottom_hash_view);
        distanceView = (TextView) rootView.findViewById(R.id.distance_view);

        Button topButton = (Button) rootView.findViewById(R.id.top_button);
        Button bottomButton = (Button) rootView.findViewById(R.id.bottomo_button);
        topButton.setOnClickListener(this);
        bottomButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.top_button:
                ImageGen topImageGen = new ImageGen();
                topImage.setImageBitmap(topImageGen.getOriginalBitmap());
                topHashView.setText(topImageGen.getHash());
                break;

            case R.id.bottomo_button:
                ImageGen bottomImageGen = new ImageGen();
                bottomImage.setImageBitmap(bottomImageGen.getOriginalBitmap());
                String mHash = bottomImageGen.getHash();
                bottomHashView.setText(mHash);
                if (topHashView.getText().length() > 0) {
                    int distance = (new LevenshteinDistance()).calculateDistance(
                            topHashView.getText().toString(),
                            mHash
                    );
                    double ratio = (double) distance * 100 / (double) mHash.length();
                    distanceView.setText(getString(R.string.distance_label, distance, ratio));
                }
                break;
        }
    }
}
