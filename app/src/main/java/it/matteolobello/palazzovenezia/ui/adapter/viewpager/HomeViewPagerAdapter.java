package it.matteolobello.palazzovenezia.ui.adapter.viewpager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import it.matteolobello.palazzovenezia.ui.fragment.home.AboutFragment;
import it.matteolobello.palazzovenezia.ui.fragment.home.MapFragment;
import it.matteolobello.palazzovenezia.ui.fragment.home.PaintingsFragment;
import it.matteolobello.palazzovenezia.ui.fragment.home.SearchFragment;

public class HomeViewPagerAdapter extends FragmentPagerAdapter {

    private final PaintingsFragment mPaintingsFragment = new PaintingsFragment();
    private final SearchFragment mSearchFragment = new SearchFragment();
    private final Fragment mQRCodeFragment = new Fragment();
    private final MapFragment mMapFragment = new MapFragment();
    private final AboutFragment mAboutFragment = new AboutFragment();

    private final Fragment[] FRAGMENTS = {
            mPaintingsFragment,
            mSearchFragment,
            mQRCodeFragment,
            mMapFragment,
            mAboutFragment
    };

    public HomeViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return FRAGMENTS[position];
    }

    @Override
    public int getCount() {
        return FRAGMENTS.length;
    }
}