package com.example.devmob.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.devmob.entity.Accommodation;

import java.util.List;
@Dao
public interface AccommodationDAO {

    @Insert
    void insertAccommodation(Accommodation accommodation);

    @Update
    void updateAccommodation(Accommodation accommodation);

    @Delete
    void deleteAccommodation(Accommodation accommodation);

    @Query("SELECT * FROM accommodation WHERE accommodationId = :id")
    Accommodation getAccommodationById(long id);

    @Query("SELECT * FROM accommodation")
    List<Accommodation> getAllAccommodations();

    // Nouvelle méthode pour renvoyer un LiveData
    @Query("SELECT * FROM accommodation")
    androidx.lifecycle.LiveData<List<Accommodation>> getAllAccommodationsLiveData();

    @Query("SELECT * FROM accommodation WHERE capacity >= :minCapacity")
    List<Accommodation> getAccommodationsByCapacity(int minCapacity);

    @Query("SELECT * FROM accommodation WHERE pricePerNight <= :maxPrice")
    List<Accommodation> getAccommodationsByPrice(double maxPrice);
}
