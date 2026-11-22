package com.android.betterway.autoscheduleactivity.daggerneed;

import androidx.fragment.app.Fragment;

import com.android.betterway.autoscheduleactivity.present.AutoSchedulePresenterImpel;

import dagger.Component;

/**
 * @author Jackdow
 * @version 1.0
 *          BetterWay
 */
@Component(modules = AutoSchedulePresenterImpelModule.class)
public interface AutoScheduleMainFragmentComponent {
    /**
     * 娉ㄥ叆渚濊禆
     * @param fragment 娉ㄥ叆渚濊禆鐨勭洰鏍噁ragment
     */
    void inject(Fragment fragment);

    /**
     * 鑾峰緱AutoSchedulePresenterImpel瀹炰緥
     * @return 杩斿洖瀹炰緥
     */
    AutoSchedulePresenterImpel getAutoAutoSchedulePresenterImpel();
}

