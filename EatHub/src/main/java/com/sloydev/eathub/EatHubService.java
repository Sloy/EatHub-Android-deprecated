package com.sloydev.eathub;


import com.sloydev.eathub.model.Recipe;
import com.sloydev.eathub.model.User;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;

public interface EatHubService {

    @GET("/recipes")
    List<Recipe> listRecipes();

    @GET("/recipes/{recipe}")
    Recipe getRecipe(@Path("recipe") String recipe);

    @GET("/users/{username}")
    User getUser(@Path("username") String username);

    @GET("/login/")
    User login(@Header("Authorization") String authorization); //FIXME el avatar se recibe nulo
}
