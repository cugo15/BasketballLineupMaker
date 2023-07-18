package com.cugo.basketballlineupmaker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface NotesDao {
    @Query("SELECT * FROM Notes")
    Flowable<List<Notes>> getAll();

    @Insert
    Completable insert(Notes notes);

    @Delete
    Completable delete(Notes notes);

    @Update
    Completable update(Notes notes);

    @Query("SELECT * FROM Notes WHERE fav = 1")
    Flowable<List<Notes>> getFav();
}
