package tn.esprit.finalproject.Network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tn.esprit.finalproject.Network.UserApiService;

public class RetrofitInstance {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/"; // Replace with your API base URL
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON parsing
                    .build();
        }
        return retrofit;
    }

    public static UserApiService getUserApiService() {
        return getRetrofitInstance().create(UserApiService.class);
    }
}
