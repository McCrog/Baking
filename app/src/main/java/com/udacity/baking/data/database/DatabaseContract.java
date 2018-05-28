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

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by McCrog on 02/04/2018.
 */

public class DatabaseContract {
    static final String AUTHORITY = "com.udacity.baking";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    static final String PATH_RECIPES = "recipes";
    static final String PATH_INGREDIENTS = "ingredients";
    static final String PATH_STEPS = "steps";

    static final class RecipeEntry implements BaseColumns {
        static final Uri CONTENT_URI_RECIPE =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();

        static final String CONTENT_LIST_TYPE_RECIPES = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + AUTHORITY + PATH_RECIPES;
        static final String CONTENT_ITEM_TYPE_RECIPES = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + AUTHORITY + PATH_RECIPES;

        static final String TABLE_RECIPES = "recipes";
        static final String RECIPE_ID = "recipe_id";
        static final String RECIPE_NAME = "name";
        static final String RECIPE_SERVINGS = "servings";
        static final String RECIPE_IMAGE = "image";
    }

    public static final class IngredientsEntry implements BaseColumns {
        public static final Uri CONTENT_URI_INGREDIENTS = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_INGREDIENTS).build();

        static final String CONTENT_LIST_TYPE_INGREDIENTS
                = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + PATH_INGREDIENTS;
        static final String CONTENT_ITEM_TYPE_INGREDIENTS
                = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + PATH_INGREDIENTS;

        static final String TABLE_INGREDIENTS = "ingredients";
        public static final String INGREDIENTS_QUANTITY = "quantity";
        public static final String INGREDIENTS_MEASURE = "measure";
        public static final String INGREDIENTS_INGREDIENT = "ingredient";
        public static final String RECIPE_ID_KEY = "recipe_id_key";
    }

    static final class StepsEntry implements BaseColumns {
        static final Uri CONTENT_URI_STEPS = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_STEPS).build();

        static final String CONTENT_LIST_TYPE_STEPS = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + AUTHORITY + PATH_STEPS;
        static final String CONTENT_ITEM_TYPE_STEPS = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + AUTHORITY + PATH_STEPS;

        static final String TABLE_STEPS = "steps";
        static final String STEPS_ID = "steps_id";
        static final String STEPS_SHORT_DESCRIP = "shortDescription";
        static final String STEPS_DESCRIP = "description";
        static final String STEPS_VIDEO = "videoURL";
        static final String STEPS_THUMB = "thumbnailURL";
        static final String RECIPE_ID_KEY = "recipe_id_key";
    }
}
