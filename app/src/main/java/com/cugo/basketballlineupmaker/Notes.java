package com.cugo.basketballlineupmaker;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Notes {
    @PrimaryKey(autoGenerate = true)
    public int nid;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "not")
    public String not;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte [] img;

    @ColumnInfo(name = "fav")
    public Boolean fav;

    public Notes(String title, String not, byte[] img, Boolean fav) {
        this.title = title;
        this.not = not;
        this.img = img;
        this.fav = fav;
    }
}
