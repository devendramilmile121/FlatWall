package com.d.h.milmile.wallpaper.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.d.h.milmile.wallpaper.Manifest;
import com.d.h.milmile.wallpaper.R;
import com.d.h.milmile.wallpaper.models.Category;
import com.d.h.milmile.wallpaper.models.Wallpaper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder> {
    private Context context;
    private List<Wallpaper> wallpaperList;

    public WallpaperAdapter(Context context, List<Wallpaper> wallpaperList) {
        this.context = context;
        this.wallpaperList = wallpaperList;
    }

    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_wallpapers,parent,false);
       return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperViewHolder holder, int position) {
        Wallpaper w = wallpaperList.get(position);
        holder.textView.setText(w.title);


        Glide.with(context)
                .load(w.url)
                .into(holder.imageView);

        if (w.isFavourite){
            Log.d("checked", "onBindViewHolder: checked or not");
            holder.checkBox.setChecked(true);
        }


    }

    @Override
    public int getItemCount() {
        return wallpaperList.size();
    }

    class WallpaperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

        TextView textView;
        ImageView imageView;

        CheckBox checkBox;
        ImageButton share,download,setWall;


        public WallpaperViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_title);
            imageView= itemView.findViewById(R.id.img_view);
            checkBox = itemView.findViewById(R.id.cb_fav);
            share = itemView.findViewById(R.id.btnshare);
            download = itemView.findViewById(R.id.btndownload);
            setWall = itemView.findViewById(R.id.btnSetWall);
            checkBox.setOnCheckedChangeListener(this);
            share.setOnClickListener(this);
            download.setOnClickListener(this);
            setWall.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnshare:
                    shareWallpaper(wallpaperList.get(getAdapterPosition()));
                    break;
                case R.id.btndownload:
                    downloadWallpaper(wallpaperList.get(getAdapterPosition()));
                    break;
                case R.id.btnSetWall:
                    setWallpaper(wallpaperList.get(getAdapterPosition()));
                    break;
            }
        }


        private void shareWallpaper(Wallpaper wallpaper){
            ((Activity)context).findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
            Glide.with(context)
                    .asBitmap()
                    .load(wallpaper.url)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            ((Activity)context).findViewById(R.id.progressbar).setVisibility(View.GONE);
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("image/*");
                            i.putExtra(Intent.EXTRA_STREAM,getLoacalBitmapUri(resource));
                            context.startActivity(Intent.createChooser(i,"Flat Wall"));
                        }
                    });
            }

            private void downloadWallpaper(final Wallpaper w){
                ((Activity)context).findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
                Glide.with(context)
                        .asBitmap()
                        .load(w.url)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                ((Activity)context).findViewById(R.id.progressbar).setVisibility(View.GONE);
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                Uri uri = saveWallpaper(resource,w.title);
                                if (uri != null) {
                                    i.setDataAndType(uri,"image/*");
                                    context.startActivity(Intent.createChooser(i,"Flat Wall"));
                                }
                            }
                        });
            }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (FirebaseAuth.getInstance().getCurrentUser() == null){
                Toast.makeText(context,"Please login first...",Toast.LENGTH_SHORT).show();
                buttonView.setChecked(false);
                return;
            }

            int position = getAdapterPosition();
            Wallpaper w = wallpaperList.get(position);

            DatabaseReference dbFav =  FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("favourites")
                    .child(w.cat);

            if (isChecked){
                dbFav.child(w.id).setValue(w);
            }else {
                dbFav.child(w.id).setValue(null);
            }
        }
    }

            private Uri saveWallpaper(Bitmap bitmap, String name){
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){


                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                    Intent h = new Intent();
                    h.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package",context.getPackageName(),null);
                    h.setData(uri);
                    context.startActivity(h);

                }else {
                        ActivityCompat.requestPermissions((Activity) context,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},212 );
                }
                return null;
            }
            File folder = new File(Environment.getExternalStorageDirectory().toString() + "/FlatWall");
            folder.mkdirs();

            File file = new File(folder,name+".jpg");
                try {
                    FileOutputStream outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                    outputStream.flush();
                    outputStream.close();

                    return Uri.fromFile(file);



                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
        }

        private Uri getLoacalBitmapUri(Bitmap bitmap){
            Uri bmpUri = null;
            try {
                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"FlatWall_"+System.currentTimeMillis()+".jpeg");
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                outputStream.close();
                bmpUri = Uri.fromFile(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmpUri;
        }


    private void setWallpaper(Wallpaper w){
        ((Activity)context).findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        Glide.with(context)
                .asBitmap()
                .load(w.url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull final Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);


                            AlertDialog.Builder builder = new AlertDialog.Builder((Activity)context);
                            builder.setMessage("Do you want to set as wallpaper ?\n\nNote : This will set wallpaper as Home screen as well as Lock screen.")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            try {
                                                wallpaperManager.setBitmap(resource);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            ((Activity)context).findViewById(R.id.progressbar).setVisibility(View.GONE);
                                            Toast.makeText(context,"Wallpaper set...",Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNegativeButton("No" ,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((Activity)context).findViewById(R.id.progressbar).setVisibility(View.GONE);
                                dialog.cancel();
                            }
                        });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.setTitle("Setting Wallpaper");
                            alertDialog.show();






                    }
                });
    };

}


