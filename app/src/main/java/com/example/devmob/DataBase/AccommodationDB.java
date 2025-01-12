package com.example.devmob.DataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.devmob.DAO.AccommodationDAO;
import com.example.devmob.entity.Accommodation;
import com.example.devmob.DAO.EventDAO;
import com.example.devmob.entity.Event;

@Database(entities = {Accommodation.class, Event.class}, version = 3)
public abstract class AccommodationDB extends RoomDatabase {
    private static AccommodationDB instance;

    public abstract AccommodationDAO accommodationDao();
    public abstract EventDAO eventDao();

    public static synchronized AccommodationDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AccommodationDB.class, "app_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}