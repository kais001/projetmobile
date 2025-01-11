package tn.esprit.finalproject.Network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UserApiService {
    @GET("users") // Replace with your endpoint
    Call<List<UserResponse>> getUsers();
}
