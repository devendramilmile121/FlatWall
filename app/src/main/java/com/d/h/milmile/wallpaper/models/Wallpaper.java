package com.d.h.milmile.wallpaper.models;

import com.google.firebase.database.Exclude;

public class Wallpaper {
   @Exclude
   public String id;

   public String title,desc,url;
   @Exclude
   public String cat;
   @Exclude
   public  boolean isFavourite = false;

   public Wallpaper(String id, String title, String desc, String url, String cat) {
      this.id = id;
      this.title = title;
      this.desc = desc;
      this.url = url;
      this.cat = cat;
   }
}
