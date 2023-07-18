package com.cugo.basketballlineupmaker;

import android.graphics.Bitmap;
import java.io.Serializable;
public class PlayersData implements Serializable {
    int id;
    String name;
    String position;
    String height;
    String age;
    String jerseynumber;
    Bitmap image;
    public String getJerseynumber() {
        return jerseynumber;
    }

    public void setJerseynumber(String jerseynumber) {
        this.jerseynumber = jerseynumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public PlayersData(int id, String name, String position, String height, String age, String jerseynumber, Bitmap image) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.height = height;
        this.age = age;
        this.jerseynumber = jerseynumber;
        this.image = image;
    }
}
