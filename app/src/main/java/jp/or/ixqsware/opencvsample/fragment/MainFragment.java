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
import jp.or.ixqsware.opencvsample.R;

import static jp.or.ixqsware.opencvsample.Constants.*;

public class MainFragment extends Fragment implements View.OnClickListener {
    private ImageView topImage;
    private ImageView bottomImage;
    private TextView topHashView;
    private TextView bottomHashView;
    private TextView distanceView;

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
                loadImage(REQUEST_TOP);
                /*
                ImageGen topImageGen = new ImageGen();
                topImage.setImageBitmap(topImageGen.getOriginalBitmap());
                topHashView.setText(topImageGen.getHash());
                 */
                break;

            case R.id.bottomo_button:
                loadImage(REQUEST_BOTTOM);
                /*
                ImageGen bottomImageGen = new ImageGen();
                bottomImage.setImageBitmap(bottomImageGen.getBitmap());
                String mHash = bottomImageGen.getHash();
                bottomHashView.setText(mHash);
                if (topHashView.getText().length() > 0) {
                    int distance = (new LevenshteinDistance()).calculateDistance(
                            topHashView.getText().toString(),
                            mHash
                    );
                    double ratio = (distance / mHash.length()) * 100.0;
                    distanceView.setText("Distance: " + distance
                            + " (" + ratio + "%)");
                }
                 */
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) { return; }
        try {
            InputStream is = getActivity().getContentResolver().openInputStream(data.getData());
            Bitmap img = BitmapFactory.decodeStream(is);
            is.close();
            ImageGen imageGen = new ImageGen(img);
            switch (requestCode) {
                case REQUEST_TOP:
                    topImage.setImageBitmap(imageGen.getOriginalBitmap());
                    topHashView.setText(imageGen.getHash());
                    break;

                case REQUEST_BOTTOM:
                    bottomImage.setImageBitmap(imageGen.getOriginalBitmap());
                    bottomHashView.setText(imageGen.getHash());
                    break;
            }
            if (topHashView.getText().length() > 0 && bottomHashView.getText().length() > 0) {
                int distance = (new LevenshteinDistance()).calculateDistance(
                        topHashView.getText().toString(),
                        bottomHashView.getText().toString()
                );
                double ratio = (double) distance * 100 / (double) bottomHashView.getText().length();
                distanceView.setText("Distance: " + distance + " (" + ratio + "%)");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadImage(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);
    }
}
