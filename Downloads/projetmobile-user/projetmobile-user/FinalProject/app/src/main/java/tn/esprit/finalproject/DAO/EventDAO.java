package tn.esprit.finalproject.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import tn.esprit.finalproject.Entity.Event;

import java.util.List;

@Dao
public interface EventDAO {

    @Insert
    void insertEvent(Event event);

    @Update
    void updateEvent(Event event);

    @Delete
    void deleteEvent(Event event);

    @Query("SELECT * FROM event WHERE eventId = :id")
    Event getEventById(long id);

    @Query("SELECT * FROM event")
    List<Event> getAllEvents();

    // Nouvelle méthode pour renvoyer un LiveData
    @Query("SELECT * FROM event")
    LiveData<List<Event>> getAllEventsLiveData();




}
