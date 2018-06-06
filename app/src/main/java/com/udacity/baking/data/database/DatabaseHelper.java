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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.udacity.baking.data.database.DatabaseContract.IngredientsEntry;
import static com.udacity.baking.data.database.DatabaseContract.RecipeEntry;
import static com.udacity.baking.data.database.DatabaseContract.StepsEntry;

/**
 * Created by McCrog on 02/04/2018.
 *
 */

class DatabaseHelper extends SQLiteOpenHelper {
    // The database name
    private static final String DATABASE_NAME = "baking_recipes.db";
    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 4;

    private static final String SQL_CREATE_RECIPES_LIST_TABLE =
            "CREATE TABLE " + RecipeEntry.TABLE_RECIPES + " (" +
                    RecipeEntry.RECIPE_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                    RecipeEntry.RECIPE_NAME + " TEXT NOT NULL, " +
                    RecipeEntry.RECIPE_SERVINGS + " INTEGER NOT NULL, " +
                    RecipeEntry.RECIPE_IMAGE + " TEXT" +
                    "); ";

    private static final String SQL_CREATE_INGREDIENTS_LIST_TABLE =
            "CREATE TABLE " + IngredientsEntry.TABLE_INGREDIENTS + " (" +
                    IngredientsEntry.INGREDIENTS_QUANTITY + " REAL NOT NULL, " +
                    IngredientsEntry.INGREDIENTS_MEASURE + " TEXT NOT NULL, " +
                    IngredientsEntry.INGREDIENTS_INGREDIENT + " TEXT NOT NULL, " +
                    IngredientsEntry.RECIPE_ID_KEY + " INTEGER NOT NULL, " +
                    "FOREIGN KEY(" + IngredientsEntry.RECIPE_ID_KEY + ") REFERENCES " +
                    RecipeEntry.TABLE_RECIPES + "(" + RecipeEntry.RECIPE_ID + ") ON DELETE CASCADE" +
                    "); ";

    private static final String SQL_CREATE_STEPS_LIST_TABLE =
            "CREATE TABLE " + StepsEntry.TABLE_STEPS + " (" +
                    StepsEntry.STEPS_ID + " INTEGER NOT NULL, " +
                    StepsEntry.STEPS_SHORT_DESCRIP + " TEXT NOT NULL, " +
                    StepsEntry.STEPS_DESCRIP + " TEXT NOT NULL, " +
                    StepsEntry.STEPS_VIDEO + " TEXT NOT NULL, " +
                    StepsEntry.STEPS_THUMB + " TEXT NOT NULL, " +
                    StepsEntry.RECIPE_ID_KEY + " INTEGER NOT NULL, " +
                    "FOREIGN KEY(" + IngredientsEntry.RECIPE_ID_KEY + ") REFERENCES " +
                    RecipeEntry.TABLE_RECIPES + "(" + RecipeEntry.RECIPE_ID + ") ON DELETE CASCADE" +
                    "); ";

    // Constructor
    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_RECIPES_LIST_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_INGREDIENTS_LIST_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STEPS_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String DROP_TABLE = "DROP TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(DROP_TABLE + RecipeEntry.TABLE_RECIPES);
        sqLiteDatabase.execSQL(DROP_TABLE + IngredientsEntry.TABLE_INGREDIENTS);
        sqLiteDatabase.execSQL(DROP_TABLE + StepsEntry.TABLE_STEPS);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }
}
