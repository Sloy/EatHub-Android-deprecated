package com.sloydev.eathub.model;


import java.io.Serializable;
import java.util.List;

public class Recipe implements Serializable {


    String id;
    String title;
    String description;
    String creation_date;
    String modification_date;
    String main_image;
    List<String> ingredients;
    List<Step> steps;
    String serves;
    String language;
    List<String> temporality;
    String nationality;
    List<String> special_conditions;
    String notes;
    Integer difficult;
    String food_type;
    List<String> tags;
    Savour savours;
    EmbeddedRecipe parent;
    EmbeddedUser author;
    List<Picture> pictures;
    Time time;
    List<Comment> comments;
    List<EmbeddedRecipe> child_recipes;


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public String getModification_date() {
        return modification_date;
    }

    public String getMainImage() {
        return main_image;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public String getServes() {
        return serves;
    }

    public String getLanguage() {
        return language;
    }

    public List<String> getTemporality() {
        return temporality;
    }

    public String getNationality() {
        return nationality;
    }

    public List<String> getSpecial_conditions() {
        return special_conditions;
    }

    public String getNotes() {
        return notes;
    }

    public Integer getDifficulty() {
        return difficult;
    }

    public String getFood_type() {
        return food_type;
    }

    public List<String> getTags() {
        return tags;
    }

    public Savour getSavours() {
        return savours;
    }

    public EmbeddedRecipe getParent() {
        return parent;
    }

    public EmbeddedUser getAuthor() {
        return author;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public Time getTime() {
        return time;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<EmbeddedRecipe> getChild_recipes() {
        return child_recipes;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", creation_date='" + creation_date + '\'' +
                ", main_image='" + main_image + '\'' +
                ", steps=" + steps +
                '}';
    }

    public static class Step implements Serializable {
        String text;
        String image;

        public String getText() {
            return text;
        }

        public String getImage() {
            return image;
        }

        @Override
        public String toString() {
            return text.length() > 20 ? text.substring(0, 20) + "..." : text;
        }
    }

    public static class Savour implements Serializable {
        Integer salty;
        Integer sour;
        Integer bitter;
        Integer sweet;
        Integer spicy;
    }

    public static class Picture implements Serializable {
        String image;
    }

    public static class Time implements Serializable {
        int prep_time;
        int cook_time;

        public int getTotal() {
            return prep_time + cook_time;
        }
        public int getPrepTime() {
            return prep_time;
        }

        public int getCookTime() {
            return cook_time;
        }
    }

    public static class Comment implements Serializable {
        String text;
        String create_date;
        EmbeddedUser user_own;

        public String getText() {
            return text;
        }

        public String getCreateDate() {
            return create_date;
        }

        public EmbeddedUser getUser() {
            return user_own;
        }
    }

    public static class EmbeddedRecipe implements Serializable {
        String id;
        String title;
        EmbeddedUser author;
        String main_image;

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public EmbeddedUser getAuthor() {
            return author;
        }

        public String getMainImage() {
            return main_image;
        }
    }
}
