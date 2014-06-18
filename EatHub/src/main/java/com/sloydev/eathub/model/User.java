package com.sloydev.eathub.model;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {


    String username;
    String email;
    Profile profile;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Profile getProfile() {
        return profile;
    }

    public static class Profile implements Serializable{
        String display_name;
        String avatar;
        String main_language;
        List<String> additional_languages;
        String website;
        String location;
        int karma;
        String gender;
        Recipe.Savour tastes;
        List<EmbeddedUser> following;

        public List<Recipe.EmbeddedRecipe> getRecipes() {
            return recipes;
        }

        public void setRecipes(List<Recipe.EmbeddedRecipe> recipes) {
            this.recipes = recipes;
        }

        List<Recipe.EmbeddedRecipe> recipes;

        public String getDisplayName() {
            return display_name;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getMainLanguage() {
            return main_language;
        }

        public List<String> getAdditionalLanguages() {
            return additional_languages;
        }

        public String getWebsite() {
            return website;
        }

        public String getLocation() {
            return location;
        }

        public int getKarma() {
            return karma;
        }

        public String getGender() {
            return gender;
        }

        public Recipe.Savour getTastes() {
            return tastes;
        }

        public List<EmbeddedUser> getFollowing() {
            return following;
        }
    }
}
