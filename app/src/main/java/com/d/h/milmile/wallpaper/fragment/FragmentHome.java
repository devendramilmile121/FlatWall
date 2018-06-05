package com.d.h.milmile.wallpaper.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.d.h.milmile.wallpaper.R;
import com.d.h.milmile.wallpaper.adapter.CategoriesAdapter;
import com.d.h.milmile.wallpaper.models.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends android.support.v4.app.Fragment {
    @Nullable

    private List<Category> categoryList;
    private DatabaseReference dbCat;
    private ProgressBar pd;
    private RecyclerView  recyclerView;
    private CategoriesAdapter categoriesAdapter ;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pd = view.findViewById(R.id.progressbar);
        pd.setVisibility(View.VISIBLE);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        categoryList = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapter(getActivity(),categoryList);
        recyclerView.setAdapter(categoriesAdapter);





        dbCat = FirebaseDatabase.getInstance().getReference("categories");
        dbCat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    pd.setVisibility(View.INVISIBLE);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String name = ds.getKey();
                        String desc = ds.child("desc").getValue(String.class);
                        String thumb = ds.child("thumbnail").getValue(String.class);

                        Category c = new Category(name,thumb, desc );
                        categoryList.add(c);
                    }

                    categoriesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
