package com.matteolobello.palazzovenezia.ui.fragment.introduction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.matteolobello.palazzovenezia.R;
import com.matteolobello.palazzovenezia.ui.activity.IntroductionActivity;
import com.matteolobello.palazzovenezia.util.SystemBarsUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WelcomeIntroductionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_intro_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final IntroductionActivity introductionActivity = (IntroductionActivity) getActivity();
        if (introductionActivity == null) {
            return;
        }

        View introButton = view.findViewById(R.id.intro_button);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) introButton.getLayoutParams();
        layoutParams.setMargins(0, 0, 0,
                layoutParams.bottomMargin + SystemBarsUtil.getNavigationBarHeight(getActivity()));
        introButton.setLayoutParams(layoutParams);
        introButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                introductionActivity.onIntroButtonClick();
            }
        });
    }
}