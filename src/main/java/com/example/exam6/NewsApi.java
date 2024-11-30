package com.example.exam6;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {
    @GET("esports/index")
    Call<NewsResponse> getNews(
            @Query("key") String key,
            @Query("num") int pageSize,
            @Query("page") int page
    );
}
