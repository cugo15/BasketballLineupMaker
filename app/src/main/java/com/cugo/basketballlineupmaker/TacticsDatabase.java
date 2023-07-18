package com.cugo.basketballlineupmaker;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Tactics.class},version =2)
public abstract class TacticsDatabase extends RoomDatabase {
public abstract TacticsDao tacticsDao();
}
