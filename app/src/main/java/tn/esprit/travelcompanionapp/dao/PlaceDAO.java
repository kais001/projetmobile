package tn.esprit.travelcompanionapp.dao;


import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;

import androidx.room.Insert;
import androidx.room.Query;


import java.util.List;

import tn.esprit.travelcompanionapp.models.Place;

@Dao
public interface PlaceDAO {

    @Insert(onConflict = REPLACE)
    void insert(Place place);

    @Query(value = "UPDATE Places SET name = :name, state = :state, description = :description, photoUrl = :photoUrl, gpsCoordinates = :gpsCoordinates WHERE id = :id")
    void update(int id, String name, String state, String description, String photoUrl, String gpsCoordinates);

    @Query(value = "DELETE FROM places WHERE id = :id")
    void delete(int id);

    @Query("SELECT * FROM places ORDER BY id DESC")
    List<Place> getAllPlaces();

}
