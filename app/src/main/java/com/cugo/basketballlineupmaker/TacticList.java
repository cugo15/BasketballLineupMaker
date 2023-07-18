package com.cugo.basketballlineupmaker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.cugo.basketballlineupmaker.databinding.ActivityMainBinding;
import com.cugo.basketballlineupmaker.databinding.ActivityTacticListBinding;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TacticList extends AppCompatActivity {
    private ActivityTacticListBinding activityTacticListBinding;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    TacticsDatabase tdb;
    TacticsDao tacticsDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTacticListBinding = ActivityTacticListBinding.inflate(getLayoutInflater());
        View view  = activityTacticListBinding.getRoot();
        setContentView(view);
        tdb = Room.databaseBuilder(getApplicationContext(),TacticsDatabase.class,"Tactics").build();
        tacticsDao =tdb.tacticsDao();
        compositeDisposable.add(tacticsDao.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(TacticList.this::handleResponse));
        Intent infint = getIntent();
        String info = infint.getStringExtra("info");

        if(info.equals("fav")){
            tacticsDao = tdb.tacticsDao();
            compositeDisposable.add(tacticsDao.getFav()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(TacticList.this::handleResponse));
            activityTacticListBinding.gofav.setVisibility(View.GONE);
            activityTacticListBinding.toolbarText.setText("Favorite Tactics");

            activityTacticListBinding.goHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TacticList.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

        }
        else{
            activityTacticListBinding.toolbarText.setText("Tactics");
            tacticsDao = tdb.tacticsDao();
            compositeDisposable.add(tacticsDao.getAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(TacticList.this::handleResponse));
            activityTacticListBinding.gofav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TacticList.this,TacticList.class);
                    intent.putExtra("info","fav");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
            activityTacticListBinding.goHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TacticList.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

        }

        activityTacticListBinding.gofav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TacticList.this,TacticList.class);
                intent.putExtra("info","fav");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });



    }

    private void handleResponse(List<Tactics> tactics){
        activityTacticListBinding.rcyctacticview.setLayoutManager(new LinearLayoutManager(this));
        TacticsAdapter tacticsAdapter =new TacticsAdapter(tactics);
        activityTacticListBinding.rcyctacticview.setAdapter(tacticsAdapter);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}