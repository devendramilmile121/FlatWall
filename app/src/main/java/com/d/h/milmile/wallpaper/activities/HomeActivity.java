package com.d.h.milmile.wallpaper.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.d.h.milmile.wallpaper.R;
import com.d.h.milmile.wallpaper.adapter.ViewPagerAdapter;
import com.d.h.milmile.wallpaper.fragment.FavFragment;
import com.d.h.milmile.wallpaper.fragment.FragmentHome;
import com.d.h.milmile.wallpaper.fragment.SettingsFragment;
import com.eftimoff.viewpagertransformers.BackgroundToForegroundTransformer;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;

public class HomeActivity extends AppCompatActivity  {

    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;
    FragmentHome fragmentHome;
    SettingsFragment settingsFragment;
    FavFragment favFragment;
    private AdView adView;



    MenuItem menuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);




        MobileAds.initialize(this,
                "ca-app-pub-4580030607911151~4173157923");

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener(){
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
            }
        });


        //view pager
        viewPager = findViewById(R.id.viewpager);
        //bottom nav
        bottomNavigationView = findViewById(R.id.bottom_nav);
        //toolbar

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_fav:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.nav_setting:
                        viewPager.setCurrentItem(2);
                        break;

                    default:
                        viewPager.setCurrentItem(0);
                        break;
                }
                return true;
            }
        });


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }

                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                menuItem = bottomNavigationView.getMenu().getItem(position);
                viewPager.getAdapter().notifyDataSetChanged();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewPager.clearOnPageChangeListeners();
    }

    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragmentHome = new FragmentHome();
        favFragment = new FavFragment();
        settingsFragment  = new SettingsFragment();

        pagerAdapter.addFragment(fragmentHome);
        pagerAdapter.addFragment(favFragment);
        pagerAdapter.addFragment(settingsFragment);

        viewPager.setAdapter(pagerAdapter);
        //viewPager.setOffscreenPageLimit(0);


        viewPager.setPageTransformer(true,new BackgroundToForegroundTransformer());
    }


    public void showInfo(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Name : Devendra Milmile\nEmail : d.h.milmile1@gmail.com\n\nIf you have any suggestion or found any bug you can contact me on email.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("About Developer");
        alertDialog.show();
    }
}

