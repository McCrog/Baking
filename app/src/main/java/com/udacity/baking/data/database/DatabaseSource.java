/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.udacity.baking.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.udacity.baking.model.Ingredient;
import com.udacity.baking.model.Recipe;
import com.udacity.baking.model.Step;

import java.util.ArrayList;
import java.util.List;

import static com.udacity.baking.data.database.DatabaseContract.IngredientsEntry;
import static com.udacity.baking.data.database.DatabaseContract.RecipeEntry;
import static com.udacity.baking.data.database.DatabaseContract.StepsEntry;

/**
 * Created by McCrog on 02/04/2018.
 *
 */

public class DatabaseSource {
    private static final String LOG_TAG = DatabaseSource.class.getSimpleName();

    private static final int RECIPE_ID = 0;
    private static final int RECIPE_NAME = 1;
    private static final int RECIPE_SERVINGS = 2;
    private static final int RECIPE_IMAGE = 3;

    private static final int INGREDIENTS_QUANTITY = 0;
    private static final int INGREDIENTS_MEASURE = 1;
    private static final int INGREDIENTS_INGREDIENT = 2;

    private static final int STEPS_ID = 0;
    private static final int STEPS_SHORT_DESCRIP = 1;
    private static final int STEPS_DESCRIP = 2;
    private static final int STEPS_VIDEO = 3;
    private static final int STEPS_THUMB = 4;

    private final ContentResolver mContentResolver;
    private final MutableLiveData<List<Recipe>> mDatabaseData;

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static DatabaseSource sInstance;

    private DatabaseSource(Context context) {
        mContentResolver = context.getContentResolver();
        mDatabaseData = new MutableLiveData<>();
    }

    /**
     * Get the singleton for this class
     */
    public static DatabaseSource getInstance(Context context) {
        Log.d(LOG_TAG, "Getting the database data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new DatabaseSource(context);
                Log.d(LOG_TAG, "Made new database data source");
            }
        }
        return sInstance;
    }

    public LiveData<List<Recipe>> getData() {
        return mDatabaseData;
    }

    public LiveData<Recipe> getRecipe(int id) {
        String idValue = String.valueOf(id);

        Cursor recipeCursor = queryData(
                RecipeEntry.RECIPE_ID,
                idValue,
                RecipeEntry.CONTENT_URI_RECIPE
        );

        Recipe recipe;
        MutableLiveData<Recipe> recipeLiveData = new MutableLiveData<>();

        // Recipes
        if (recipeCursor.moveToFirst()) {
            Integer recipeId = recipeCursor.getInt(RECIPE_ID);
            String name = recipeCursor.getString(RECIPE_NAME);
            Integer servings = recipeCursor.getInt(RECIPE_SERVINGS);
            String image = recipeCursor.getString(RECIPE_IMAGE);

            // Ingredients
            List<Ingredient> ingredients = new ArrayList<>();

            Cursor ingredientsCursor = queryData(
                    IngredientsEntry.RECIPE_ID_KEY,
                    idValue,
                    IngredientsEntry.CONTENT_URI_INGREDIENTS
            );

            while (ingredientsCursor.moveToNext()) {
                Double quantity = ingredientsCursor.getDouble(INGREDIENTS_QUANTITY);
                String measure = ingredientsCursor.getString(INGREDIENTS_MEASURE);
                String ingredient = ingredientsCursor.getString(INGREDIENTS_INGREDIENT);

                ingredients.add(new Ingredient(quantity, measure, ingredient));
            }
            ingredientsCursor.close();

            // Steps
            List<Step> steps = new ArrayList<>();

            Cursor stepsCursor = queryData(
                    StepsEntry.RECIPE_ID_KEY,
                    idValue,
                    StepsEntry.CONTENT_URI_STEPS
            );

            while (stepsCursor.moveToNext()) {
                Integer stepId = stepsCursor.getInt(STEPS_ID);
                String shortDescription = stepsCursor.getString(STEPS_SHORT_DESCRIP);
                String description = stepsCursor.getString(STEPS_DESCRIP);
                String videoURL = stepsCursor.getString(STEPS_VIDEO);
                String thumbnailURL = stepsCursor.getString(STEPS_THUMB);

                steps.add(new Step(stepId, shortDescription, description, videoURL, thumbnailURL));
            }
            stepsCursor.close();

            recipe = new Recipe(recipeId, name, ingredients, steps, servings, image);
            recipeLiveData.postValue(recipe);
        }
        recipeCursor.close();

        return recipeLiveData;
    }

    public void deleteRecipe(int id) {
        String mSelectionClause = RecipeEntry.RECIPE_ID + " =?";
        String[] mSelectionArgs = {String.valueOf(id)};
        mContentResolver.delete(RecipeEntry.CONTENT_URI_RECIPE, mSelectionClause, mSelectionArgs);
    }

    public void saveRecipes(List<Recipe> list) {
        // Recipes
        for (int i = 0; i < list.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(RecipeEntry.RECIPE_ID, list.get(i).getId());
            values.put(RecipeEntry.RECIPE_NAME, list.get(i).getName());
            values.put(RecipeEntry.RECIPE_SERVINGS, list.get(i).getServings());
            values.put(RecipeEntry.RECIPE_IMAGE, list.get(i).getImage());

            mContentResolver.insert(RecipeEntry.CONTENT_URI_RECIPE, values);

            // Ingredients
            List<Ingredient> ingredients = list.get(i).getIngredients();

            for (int j = 0; j < ingredients.size(); j++) {
                ContentValues ingredientValues = new ContentValues();
                ingredientValues.put(IngredientsEntry.INGREDIENTS_QUANTITY, ingredients.get(j).getQuantity());
                ingredientValues.put(IngredientsEntry.INGREDIENTS_MEASURE, ingredients.get(j).getMeasure());
                ingredientValues.put(IngredientsEntry.INGREDIENTS_INGREDIENT, ingredients.get(j).getIngredient());
                ingredientValues.put(IngredientsEntry.RECIPE_ID_KEY, list.get(i).getId());

                mContentResolver.insert(IngredientsEntry.CONTENT_URI_INGREDIENTS, ingredientValues);
            }

            // Steps
            List<Step> steps = list.get(i).getSteps();

            for (int k = 0; k < steps.size(); k++) {
                ContentValues stepValues = new ContentValues();
                stepValues.put(StepsEntry.STEPS_ID, steps.get(k).getId());
                stepValues.put(StepsEntry.STEPS_SHORT_DESCRIP, steps.get(k).getShortDescription());
                stepValues.put(StepsEntry.STEPS_DESCRIP, steps.get(k).getDescription());
                stepValues.put(StepsEntry.STEPS_VIDEO, steps.get(k).getVideoURL());
                stepValues.put(StepsEntry.STEPS_THUMB, steps.get(k).getThumbnailURL());
                stepValues.put(StepsEntry.RECIPE_ID_KEY, list.get(i).getId());

                mContentResolver.insert(StepsEntry.CONTENT_URI_STEPS, stepValues);
            }
        }
    }

    public void deleteRecipes() {
        mContentResolver.delete(RecipeEntry.CONTENT_URI_RECIPE, null, null);
    }

    public boolean isExist(int id) {
        String idValue = String.valueOf(id);

        Cursor recipeCursor = queryData(
                RecipeEntry.RECIPE_ID,
                idValue,
                RecipeEntry.CONTENT_URI_RECIPE
        );

        boolean isExist = false;

        if (recipeCursor.moveToFirst()) {
            isExist = true;
        }
        recipeCursor.close();

        return isExist;
    }

    public void loadData() {
        List<Recipe> recipes = new ArrayList<>();

        Cursor recipeCursor = mContentResolver.query(RecipeEntry.CONTENT_URI_RECIPE,
                null,
                null,
                null,
                null
        );

        // Recipes
        while (recipeCursor.moveToNext()) {
            Integer id = recipeCursor.getInt(RECIPE_ID);
            String name = recipeCursor.getString(RECIPE_NAME);
            Integer servings = recipeCursor.getInt(RECIPE_SERVINGS);
            String image = recipeCursor.getString(RECIPE_IMAGE);

            String idValue = String.valueOf(id);

            // Ingredients
            List<Ingredient> ingredients = new ArrayList<>();

            Cursor ingredientsCursor = queryData(
                    IngredientsEntry.RECIPE_ID_KEY,
                    idValue,
                    IngredientsEntry.CONTENT_URI_INGREDIENTS
            );

            while (ingredientsCursor.moveToNext()) {
                Double quantity = ingredientsCursor.getDouble(INGREDIENTS_QUANTITY);
                String measure = ingredientsCursor.getString(INGREDIENTS_MEASURE);
                String ingredient = ingredientsCursor.getString(INGREDIENTS_INGREDIENT);

                ingredients.add(new Ingredient(quantity, measure, ingredient));
            }
            ingredientsCursor.close();

            // Steps
            List<Step> steps = new ArrayList<>();

            Cursor stepsCursor = queryData(
                    StepsEntry.RECIPE_ID_KEY,
                    idValue,
                    StepsEntry.CONTENT_URI_STEPS
            );

            while (stepsCursor.moveToNext()) {
                Integer stepId = stepsCursor.getInt(STEPS_ID);
                String shortDescription = stepsCursor.getString(STEPS_SHORT_DESCRIP);
                String description = stepsCursor.getString(STEPS_DESCRIP);
                String videoURL = stepsCursor.getString(STEPS_VIDEO);
                String thumbnailURL = stepsCursor.getString(STEPS_THUMB);

                steps.add(new Step(stepId, shortDescription, description, videoURL, thumbnailURL));
            }
            stepsCursor.close();

            recipes.add(new Recipe(id, name, ingredients, steps, servings, image));
        }
        recipeCursor.close();
        mDatabaseData.setValue(recipes);
    }

    private Cursor queryData(String clause, String args, Uri uri) {
        String selectionClause = clause + " =?";
        String[] selectionArgs = {args};
        return mContentResolver.query(
                uri,
                null,
                selectionClause,
                selectionArgs,
                null
        );
    }
}
