package com.d.h.milmile.wallpaper.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FavFragment extends Fragment {

    List<Wallpaper> favWallpaper;


    RecyclerView recyclerView;
    ProgressBar progressBar ;
    WallpaperAdapter adapter;

    DatabaseReference dbFav;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fav,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favWallpaper = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progressbar);

        adapter = new WallpaperAdapter(getActivity(),favWallpaper);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);


        //check if user is not logged in
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_area,new SettingsFragment())
                    .commit();
            return;
        }

        dbFav = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("favourites");
        progressBar.setVisibility(View.VISIBLE);
        dbFav.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.INVISIBLE);
                for (DataSnapshot category:dataSnapshot.getChildren()){
                    for (DataSnapshot wallpaper:category.getChildren()){

                            String id = wallpaper.getKey();
                            String title = wallpaper.child("title").getValue(String.class);
                            String desc = wallpaper.child("desc").getValue(String.class);
                            String url = wallpaper.child("url").getValue(String.class);

                            Wallpaper w = new Wallpaper(id,title,desc,url,category.getKey());
                            w.isFavourite = true;
                            favWallpaper.add(w);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

}
