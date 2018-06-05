package com.d.h.milmile.wallpaper.activities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;


import com.d.h.milmile.wallpaper.R;
import com.d.h.milmile.wallpaper.adapter.WallpaperAdapter;
import com.d.h.milmile.wallpaper.models.Wallpaper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WallpaperActivity extends AppCompatActivity {

    List<Wallpaper> wallpaperList;
    List<Wallpaper> favList;
    RecyclerView recyclerView;
    WallpaperAdapter adapter;
    DatabaseReference dbWallpaper,dbFav;
    ProgressBar pd;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        Intent intent = getIntent();
        final String cat = intent.getStringExtra("cat");
        favList = new ArrayList<>();
        wallpaperList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WallpaperAdapter(this,wallpaperList);

        recyclerView.setAdapter(adapter);

        pd = findViewById(R.id.progressbar);



        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(cat);
        setSupportActionBar(toolbar);

        dbWallpaper = FirebaseDatabase.getInstance().getReference("images")
                .child(cat);


        if (FirebaseAuth.getInstance().getCurrentUser()!= null){
            dbFav = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .child("favourites")
            .child(cat);
            fetchFavWallpaper(cat);
        }else {
            fetchWallpaper(cat);
        }
    }

    private void fetchFavWallpaper(final String  cat){
        pd.setVisibility(View.VISIBLE);
        dbFav.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pd.setVisibility(View.INVISIBLE);
                if (dataSnapshot.exists()){
                    for (DataSnapshot wallpaperSnap:dataSnapshot.getChildren()){
                        String id = wallpaperSnap.getKey();
                        String title = wallpaperSnap.child("title").getValue(String.class);
                        String desc = wallpaperSnap.child("desc").getValue(String.class);
                        String url = wallpaperSnap.child("url").getValue(String.class);

                        Wallpaper w = new Wallpaper(id,title,desc,url,cat);
                        favList.add(w);
                    }
                }
                fetchWallpaper(cat);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void fetchWallpaper(final String cat){
        pd.setVisibility(View.VISIBLE);
        dbWallpaper.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pd.setVisibility(View.INVISIBLE);
                if (dataSnapshot.exists()){
                    for (DataSnapshot wallpaperSnap:dataSnapshot.getChildren()){
                        String id = wallpaperSnap.getKey();
                        String title = wallpaperSnap.child("title").getValue(String.class);
                        String desc = wallpaperSnap.child("desc").getValue(String.class);
                        String url = wallpaperSnap.child("url").getValue(String.class);

                        Wallpaper w = new Wallpaper(id,title,desc,url,cat);

                        if (isFavourite(w)){
                            w.isFavourite = true;
                            Log.d("checked", "onBindViewHolder: checked or not");
                        }

                        wallpaperList.add(w);

                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private boolean isFavourite(Wallpaper w){
        for (Wallpaper f:favList){
            if (f.id.equals(w.id)){

                Log.d("checked", "onBindViewHolder: checked or not");
                return true;
            }
        }
        return false;
    }
}
