package jp.or.ixqsware.opencvsample;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

import jp.or.ixqsware.opencvsample.fragment.DrawingFragment;
import jp.or.ixqsware.opencvsample.fragment.FeaturePointFragment;
import jp.or.ixqsware.opencvsample.fragment.NavigationDrawerFragment;
import jp.or.ixqsware.opencvsample.fragment.RandomFragment;
import jp.or.ixqsware.opencvsample.fragment.SelectFileFragment;

import static jp.or.ixqsware.opencvsample.Constants.*;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks{
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private ArrayList<Fragment> arrFragments = new ArrayList<>();
    private ArrayList<String> arrFragmentTags = new ArrayList<>();
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mTitle = getTitle();

        frameLayout = (FrameLayout) this.findViewById(R.id.container);
        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) { return true; }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        arrFragments.add(RandomFragment.newInstance(RANDOM_SECTION_ID));
        arrFragments.add(SelectFileFragment.newInstance(SELECT_FILE_SECTION_ID));
        arrFragments.add(DrawingFragment.newInstance(DRAWING_SECTION_ID));
        arrFragments.add(FeaturePointFragment.newInstance(FEATURE_POINT_SECTION_ID));

        arrFragmentTags.add(getString(R.string.random_fragment_name));
        arrFragmentTags.add(getString(R.string.select_file_fragment_name));
        arrFragmentTags.add(getString(R.string.drawing_fragment_name));
        arrFragmentTags.add(getString(R.string.feature_point_fragment_name));

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.popBackStack();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack(
                    fragmentManager.getBackStackEntryAt(0).getName(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
            );
        }
        fragmentManager.executePendingTransactions();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(
                    R.anim.slide_in_from_left,
                    R.anim.slide_out_to_right,
                    R.anim.slide_in_from_left,
                    R.anim.slide_out_to_left
                )
                .replace(R.id.container, arrFragments.get(position), arrFragmentTags.get(position))
                .commit();
    }

    public void setTitle(String title) {
        mTitle = title;
        restoreActionBar();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case RANDOM_SECTION_ID:
                mTitle = getString(R.string.random_fragment_name);
                break;
            case SELECT_FILE_SECTION_ID:
                mTitle = getString(R.string.select_file_fragment_name);
                break;
            case DRAWING_SECTION_ID:
                mTitle = getString(R.string.drawing_fragment_name);
                break;
            case FEATURE_POINT_SECTION_ID:
                mTitle = getString(R.string.feature_point_fragment_name);
                break;
        }
    }
}
