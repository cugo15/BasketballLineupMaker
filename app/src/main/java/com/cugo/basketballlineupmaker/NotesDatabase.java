package com.cugo.basketballlineupmaker;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Notes.class},version = 1)
public abstract class NotesDatabase extends RoomDatabase {
    public abstract NotesDao notesDao();
}
