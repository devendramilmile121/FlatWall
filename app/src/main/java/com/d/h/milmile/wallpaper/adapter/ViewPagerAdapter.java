package com.d.h.milmile.wallpaper.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.d.h.milmile.wallpaper.fragment.FavFragment;
import com.d.h.milmile.wallpaper.fragment.FragmentHome;
import com.d.h.milmile.wallpaper.fragment.SettingsFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }
    @Override
    public Fragment getItem(int position) {
        if (position==0){
            return new FragmentHome();
        }else
        if (position==1){
            return new FavFragment();
        }else

            return new SettingsFragment();
        }


    @Override
    public int getCount() {
        return 3;
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
