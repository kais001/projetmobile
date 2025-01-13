package tn.esprit.finalproject.Database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import tn.esprit.finalproject.DAO.PlaceDAO;
import tn.esprit.finalproject.DAO.UserDao;
import tn.esprit.finalproject.DAO.AccommodationDAO;
import tn.esprit.finalproject.DAO.EventDAO;
import tn.esprit.finalproject.DAO.RideDao;

import tn.esprit.finalproject.Entity.Place;
import tn.esprit.finalproject.Entity.User;
import tn.esprit.finalproject.Entity.Accommodation;
import tn.esprit.finalproject.Entity.Event;
import tn.esprit.finalproject.Entity.Ride;

// Fusion of both databases
@Database(
        entities = {User.class, Accommodation.class, Event.class, Place.class, Ride.class},
        version = 5, // Updated version to reflect schema changes
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    // DAOs
    public abstract UserDao userDao();
    public abstract AccommodationDAO accommodationDao();
    public abstract EventDAO eventDao();
    public abstract PlaceDAO placeDao();
    public abstract RideDao rideDao(); // Added RideDao from the first database

    // Singleton pattern for database instance
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .fallbackToDestructiveMigration() // Handles schema changes destructively
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
