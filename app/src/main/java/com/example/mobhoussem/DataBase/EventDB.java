package com.example.mobhoussem.DataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mobhoussem.DAO.EventDAO;
import com.example.mobhoussem.entity.Event;

@Database(entities = {Event.class}, version = 2)
public abstract class EventDB extends RoomDatabase {
    private static EventDB instance;

    public abstract EventDAO eventDao();

    // Updated method to ensure context is passed properly
    public static synchronized EventDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            EventDB.class, "event_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
