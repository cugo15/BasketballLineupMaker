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
public interface TacticsDao {

    @Query("SELECT * FROM Tactics")
    Flowable<List<Tactics>> getAll();
    @Insert
    Completable insert(Tactics tactics);
    @Delete
    Completable delete(Tactics tactics);
    @Update
    Completable update(Tactics tactics);

    @Query("SELECT * FROM Tactics WHERE fav = 1")
    Flowable<List<Tactics>> getFav();


}
