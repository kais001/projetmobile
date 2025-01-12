package tn.esprit.travelcompanionapp.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import tn.esprit.travelcompanionapp.dao.PlaceDAO;
import tn.esprit.travelcompanionapp.models.Place;

@Database(entities = {Place.class}, version = 1, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {

    private static RoomDB db;
    private static final String DB_NAME = "travel_companion_app_db";

    public synchronized static RoomDB getInstance(Context context) {


        if (db == null) {
            db = Room.databaseBuilder(
                    context.getApplicationContext(),
                    RoomDB.class, DB_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
    }
        return db;
    }

    public abstract PlaceDAO placeDAO();
}
