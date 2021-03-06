package it.matteolobello.palazzovenezia.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.os.BuildCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import it.matteolobello.palazzovenezia.R;
import it.matteolobello.palazzovenezia.data.bundle.BundleKeys;
import it.matteolobello.palazzovenezia.ui.adapter.viewpager.HomeViewPagerAdapter;
import it.matteolobello.palazzovenezia.ui.view.MaterialDesignViewPager;
import it.matteolobello.palazzovenezia.util.PermissionUtil;
import it.matteolobello.palazzovenezia.util.SystemBarsUtil;

public class HomeActivity extends AppCompatActivity {

    public static final int BLUR_OVERLAY_COLOR = Color.argb(175, 255, 255, 255);

    private static final String SHORTCUT_ACTION_SEARCH = "search";
    private static final String SHORTCUT_ACTION_MAP = "map";

    private MaterialDesignViewPager mViewPager;
    private BlurView mBlurView;
    private BottomNavigationView mBottomNavigationView;

    private HomeViewPagerAdapter mViewPagerAdapter;

    private int mBeforeQrCodeReadingFragmentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SystemBarsUtil.setNoLimitsSystemBars(this);
        SystemBarsUtil.setDarkStatusBarIcons(this, true);
        SystemBarsUtil.setDarkNavigationBarIcons(this, true);

        mViewPager = findViewById(R.id.home_view_pager);
        mBlurView = findViewById(R.id.blur_view);
        mBottomNavigationView = findViewById(R.id.bottom_navigation_view);

        if (BuildCompat.isAtLeastO()) {
            mBottomNavigationView.setBackgroundColor(Color.TRANSPARENT);
            mBottomNavigationView.setElevation(0f);

            mBlurView.setPadding(0, 0, 0, SystemBarsUtil.getNavigationBarHeight(HomeActivity.this));

            View decorView = getWindow().getDecorView();
            ViewGroup rootView = decorView.findViewById(android.R.id.content);
            Drawable windowBackground = decorView.getBackground();
            mBlurView.setupWith(rootView)
                    .setFrameClearDrawable(windowBackground)
                    .setBlurAlgorithm(new RenderScriptBlur(this))
                    .setBlurRadius(20f)
                    .setOverlayColor(BLUR_OVERLAY_COLOR)
                    .setHasFixedTransformationMatrix(true);
        }

        mViewPagerAdapter = new HomeViewPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(mViewPagerAdapter.getCount());

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (mViewPager.isSwitchingFragment()) {
                    return false;
                }

                int index = 0;
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        index = 0;
                        break;
                    case R.id.action_search:
                        index = 1;
                        break;
                    case R.id.action_scan:
                        index = 2;
                        break;
                    case R.id.action_map:
                        index = 3;
                        break;
                    case R.id.action_about:
                        index = 4;
                        break;
                }

                if (index == 2) {
                    if (!PermissionUtil.hasPermissions(getApplicationContext())) {
                        ActivityCompat.requestPermissions(HomeActivity.this, PermissionUtil.PERMISSIONS, PermissionUtil.CAMERA_PERMISSIONS_REQUEST_CODE);
                        return false;
                    }

                    mBeforeQrCodeReadingFragmentIndex = mViewPager.getCurrentItem() != 2
                            ? mViewPager.getCurrentItem()
                            : mBeforeQrCodeReadingFragmentIndex;

                    mViewPager.setCurrentItem(2, true, new MaterialDesignViewPager.OnFragmentSetListener() {
                        @Override
                        public void onFragmentSet() {
                            hideBottomNavigationViewAndStartQrCodeActivity();
                        }
                    });
                } else {
                    mViewPager.setCurrentItem(index, true);
                }

                return true;
            }
        });

        String action = getIntent().getStringExtra(BundleKeys.EXTRA_SHORTCUT_ACTION);
        if (action != null) {
            switch (action) {
                case SHORTCUT_ACTION_SEARCH:
                    changeTabSelection(1);
                    break;
                case SHORTCUT_ACTION_MAP:
                    changeTabSelection(3);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() != 0) {
            changeTabSelection(0);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionUtil.CAMERA_PERMISSIONS_REQUEST_CODE) {
            boolean permissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionsGranted = false;

                    break;
                }
            }

            if (permissionsGranted) {
                changeTabSelection(2);
                hideBottomNavigationViewAndStartQrCodeActivity();
            } else {
                changeTabSelection(0);
                Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mBottomNavigationView.getTranslationY() == mBottomNavigationView.getHeight()) {
            mBottomNavigationView.setTranslationY(0);
            changeTabSelection(mBeforeQrCodeReadingFragmentIndex);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == QRCodeActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Intent intent = new Intent(this, PaintingActivity.class);
            intent.putExtra(BundleKeys.EXTRA_PAINTING, data.getParcelableExtra(BundleKeys.EXTRA_PAINTING));
            startActivity(intent);
        }
    }

    private void changeTabSelection(int index) {
        int tabItemId = R.id.action_home;
        switch (index) {
            case 0:
                tabItemId = R.id.action_home;
                break;
            case 1:
                tabItemId = R.id.action_search;
                break;
            case 2:
                tabItemId = R.id.action_scan;
                break;
            case 3:
                tabItemId = R.id.action_map;
                break;
            case 4:
                tabItemId = R.id.action_about;
                break;
        }

        mBottomNavigationView.setSelectedItemId(tabItemId);
    }

    private void hideBottomNavigationViewAndStartQrCodeActivity() {
        mBottomNavigationView.animate()
                .translationY(mBottomNavigationView.getHeight())
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        startActivityForResult(new Intent(getApplicationContext(), QRCodeActivity.class), QRCodeActivity.REQUEST_CODE);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                })
                .start();
    }
}
