package com.example.devmob.DataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.devmob.DAO.AccommodationDAO;
import com.example.devmob.entity.Accommodation;

@Database(entities = {Accommodation.class}, version = 2)
public abstract class AccommodationDB extends RoomDatabase {
    private static AccommodationDB instance;

    public abstract AccommodationDAO accommodationDao();

    // Updated method to ensure context is passed properly
    public static synchronized AccommodationDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AccommodationDB.class, "accommodation_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
