package tn.esprit.cov.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import tn.esprit.cov.Entity.Ride;

import androidx.room.Delete;

import androidx.room.Update;

import java.util.List;

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




