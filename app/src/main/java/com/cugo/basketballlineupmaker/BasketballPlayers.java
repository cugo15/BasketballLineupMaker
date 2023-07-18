package com.cugo.basketballlineupmaker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.cugo.basketballlineupmaker.databinding.ActivityBasketballPlayersBinding;

import java.util.ArrayList;

public class BasketballPlayers extends AppCompatActivity {

    private ActivityBasketballPlayersBinding binding;

    ArrayList<PlayersData> arrayList;
    PlayerAdapter playerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBasketballPlayersBinding.inflate(getLayoutInflater());
        View view  = binding.getRoot();
        setContentView(view);
            arrayList = new ArrayList<>();
            binding.rcw.setLayoutManager(new LinearLayoutManager(this));
            playerAdapter = new PlayerAdapter(arrayList);
            binding.rcw.setAdapter(playerAdapter);
        binding.amenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });
        getData();
    }
    private void getData(){
        try{
            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("Players",MODE_PRIVATE,null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT*FROM players",null);
            int nameIX = cursor.getColumnIndex("name");
            int idIX =cursor.getColumnIndex("id");
            int heightIX =cursor.getColumnIndex("height");
            int ageIX =cursor.getColumnIndex("age");
            int posIX =cursor.getColumnIndex("position");
            int imageIX =cursor.getColumnIndex("image");
            int jerIX =cursor.getColumnIndex("jerseynumber");

            while(cursor.moveToNext()){
                String name = cursor.getString(nameIX);
                int id = cursor.getInt(idIX);
                String position = cursor.getString(posIX);
                String height = cursor.getString(heightIX);
                String age = cursor.getString(ageIX);
                String jerseynumber = cursor.getString(jerIX);
                byte[] image = cursor.getBlob(imageIX);
                Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
                PlayersData playersData = new PlayersData(id,name,position,height,age,jerseynumber,bitmap);
                arrayList.add(playersData);
            }
            playerAdapter.notifyDataSetChanged();
            cursor.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.player_menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.add_player){
                    Intent intent = new Intent(BasketballPlayers.this,CreatePlayer.class);
                    intent.putExtra("info","new");
                    startActivity(intent);
                }
                if(menuItem.getItemId()==R.id.ext){
                    finishAndRemoveTask();
                    finishAffinity();
                    System.exit(0);
                }
                return false;
            }

        });
    }
}