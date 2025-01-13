package tn.esprit.finalproject.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import tn.esprit.finalproject.Entity.Ride;

@Dao
public interface RideDao {
    @Insert
    void insertRide(Ride ride);

    @Update
    void updateRide(Ride ride);

    @Delete
    void deleteRide(Ride ride);

    @Query("SELECT * FROM rides")
    List<Ride> getAllRides();

    @Query("SELECT * FROM rides WHERE id = :id")
    Ride getRideById(int id);


    }




