package com.cugo.basketballlineupmaker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cugo.basketballlineupmaker.databinding.ActivityNoteListBinding;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NoteList extends AppCompatActivity {
    ActivityNoteListBinding noteListBinding;
    NotesDatabase nd;
    NotesDao notesDao;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noteListBinding = ActivityNoteListBinding.inflate(getLayoutInflater());
        setContentView(noteListBinding.getRoot());
        nd= Room.databaseBuilder(getApplicationContext(),NotesDatabase.class,"Notes").build();
        notesDao = nd.notesDao();
        compositeDisposable.add(notesDao.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(NoteList.this::handleResponse));
        Intent infint = getIntent();
        String info = infint.getStringExtra("infoo");

        if(info.equals("fav")){
            notesDao = nd.notesDao();
            compositeDisposable.add(notesDao.getFav()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(NoteList.this::handleResponse));
            noteListBinding.gofavnotes.setVisibility(View.GONE);
            noteListBinding.textView7.setText("Favorite Notes");



        }
        else if(info.equals("all")){
            noteListBinding.textView7.setText("Notes");
            notesDao = nd.notesDao();
            compositeDisposable.add(notesDao.getAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(NoteList.this::handleResponse));
            noteListBinding.gofavnotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(NoteList.this,NoteList.class);
                    intent.putExtra("infoo","fav");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

        }
        noteListBinding.gofavnotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteList.this,NoteList.class);
                intent.putExtra("infoo","fav");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        noteListBinding.gohomefromnotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteList.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }
    private void handleResponse(List<Notes> notesList){
        noteListBinding.rww.setLayoutManager(new LinearLayoutManager(this));
        NotesAdapter notesAdapter = new NotesAdapter(notesList);
        noteListBinding.rww.setAdapter(notesAdapter);
    }
}