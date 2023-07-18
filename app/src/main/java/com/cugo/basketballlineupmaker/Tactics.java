package com.cugo.basketballlineupmaker;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Tactics {
    @PrimaryKey(autoGenerate = true)
    public int pid;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "note")
    public String note;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte [] img;
    @ColumnInfo(name = "fav")
    public Boolean fav;

    public Tactics(String title, String note, byte[] img, Boolean fav) {
        this.title = title;
        this.note = note;
        this.img = img;
        this.fav = fav;
    }
}
