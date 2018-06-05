package com.d.h.milmile.wallpaper.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;



import com.bumptech.glide.Glide;
import com.d.h.milmile.wallpaper.R;
import com.d.h.milmile.wallpaper.activities.WallpaperActivity;
import com.d.h.milmile.wallpaper.models.Category;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;

    private InterstitialAd mInterstitialAd;


    public CategoriesAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId("ca-app-pub-4580030607911151/1874297640");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_categories,parent,false);
       return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category c = categoryList.get(position);
        holder.textView.setText(c.name);


        Glide.with(context)
                .load(c.thumb)
                .into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textView;
        ImageView imageView;

        public CategoryViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.tv_catname);
            imageView= itemView.findViewById(R.id.img_view);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }
            int p = getAdapterPosition();
            Category c = categoryList.get(p);

            Intent i = new Intent(context, WallpaperActivity.class);
            i.putExtra("cat",c.name);
            context.startActivity(i);
        }
    }
}
