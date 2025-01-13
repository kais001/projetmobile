package tn.esprit.cov.Database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import tn.esprit.cov.DAO.RideDao;
import tn.esprit.cov.Entity.Ride;

@Database(entities = {Ride.class}, version = 2)  // Increment the version number here
public abstract class AppDatabase extends RoomDatabase {
    public abstract RideDao rideDao();
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "ride_database")
                            .fallbackToDestructiveMigration() // To handle schema changes destructively
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

