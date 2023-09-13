package me.drblau.money.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.Locale;

import me.drblau.money.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStateAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.month, R.string.year, R.string.all, R.string.settings};
    private final Context mContext;

    public SectionsPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Context context) {
        super(fragmentManager, lifecycle);
        mContext = context;
    }


    @Nullable
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String title = mContext.getString(TAB_TITLES[position]);
        return BaseFragment.newInstance(title.toLowerCase(Locale.ROOT), position);
    }

    @Override
    public int getItemCount() {
        return TAB_TITLES.length;
    }
}