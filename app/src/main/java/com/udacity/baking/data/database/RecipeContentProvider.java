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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.udacity.baking.data.database.DatabaseContract.AUTHORITY;
import static com.udacity.baking.data.database.DatabaseContract.IngredientsEntry;
import static com.udacity.baking.data.database.DatabaseContract.PATH_INGREDIENTS;
import static com.udacity.baking.data.database.DatabaseContract.PATH_RECIPES;
import static com.udacity.baking.data.database.DatabaseContract.PATH_STEPS;
import static com.udacity.baking.data.database.DatabaseContract.RecipeEntry;
import static com.udacity.baking.data.database.DatabaseContract.StepsEntry;

/**
 * Created by McCrog on 03/04/2018.
 *
 */

public class RecipeContentProvider extends ContentProvider {

    private static final int RECIPES = 100;
    private static final int RECIPES_WITH_ID = 101;

    private static final int STEPS = 200;
    private static final int STEPS_WITH_ID = 201;

    private static final int INGREDIENTS = 300;
    private static final int INGREDIENTS_WITH_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, PATH_RECIPES, RECIPES);
        uriMatcher.addURI(AUTHORITY, PATH_RECIPES + "/#", RECIPES_WITH_ID);
        uriMatcher.addURI(AUTHORITY, PATH_INGREDIENTS, INGREDIENTS);
        uriMatcher.addURI(AUTHORITY, PATH_INGREDIENTS + "/#", INGREDIENTS_WITH_ID);
        uriMatcher.addURI(AUTHORITY, PATH_STEPS, STEPS);
        uriMatcher.addURI(AUTHORITY, PATH_STEPS + "/#", STEPS_WITH_ID);

        return uriMatcher;
    }

    private DatabaseHelper mDatabaseHelper;

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor;

        switch (match) {
            case RECIPES:
                cursor = queryItems(db, RecipeEntry.TABLE_RECIPES, projection, selection, selectionArgs, sortOrder);
                break;
            case RECIPES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String recipeSelection = "recipe_id=?";
                String[] recipeSelectionArgs = new String[]{id};

                cursor = queryItems(db, RecipeEntry.TABLE_RECIPES, projection, recipeSelection, recipeSelectionArgs, sortOrder);
                break;
            case INGREDIENTS:
                cursor = queryItems(db, IngredientsEntry.TABLE_INGREDIENTS, projection, selection, selectionArgs, sortOrder);
                break;
            case INGREDIENTS_WITH_ID:
                String ingredientId = uri.getPathSegments().get(1);
                String ingredientSelection = "recipe_id_key=?";
                String[] ingridientSelectionArgs = new String[]{ingredientId};

                cursor = queryItems(db, IngredientsEntry.TABLE_INGREDIENTS, projection, ingredientSelection, ingridientSelectionArgs, sortOrder);
                break;
            case STEPS:
                cursor = queryItems(db, StepsEntry.TABLE_STEPS, projection, selection, selectionArgs, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case RECIPES: {
                returnUri = insertItem(db, RecipeEntry.TABLE_RECIPES, contentValues, RecipeEntry.CONTENT_URI_RECIPE);
                break;
            }
            case INGREDIENTS: {
                returnUri = insertItem(db, IngredientsEntry.TABLE_INGREDIENTS, contentValues, IngredientsEntry.CONTENT_URI_INGREDIENTS);
                break;
            }
            case STEPS: {
                returnUri = insertItem(db, StepsEntry.TABLE_STEPS, contentValues, StepsEntry.CONTENT_URI_STEPS);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        db.close();

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int numRowsDeleted;

        switch (match) {
            case RECIPES:
                numRowsDeleted = db.delete(RecipeEntry.TABLE_RECIPES, selection, selectionArgs);
                break;
            case RECIPES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "recipe_id=?";
                String[] mSelectionArgs = new String[]{id};

                numRowsDeleted = db.delete(RecipeEntry.TABLE_RECIPES, mSelection, mSelectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        db.close();

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int numRowsUpdated;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case RECIPES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "movieId=?";
                String[] mSelectionArgs = new String[]{id};

                numRowsUpdated = db.update(RecipeEntry.TABLE_RECIPES, contentValues, mSelection, mSelectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        db.close();

        if (numRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case RECIPES: {
                return bulkInsertItems(db, RecipeEntry.TABLE_RECIPES, values, RecipeEntry.CONTENT_URI_RECIPE);
            }

            case INGREDIENTS: {
                return bulkInsertItems(db, IngredientsEntry.TABLE_INGREDIENTS, values, IngredientsEntry.CONTENT_URI_INGREDIENTS);
            }

            case STEPS: {
                return bulkInsertItems(db, StepsEntry.TABLE_STEPS, values, StepsEntry.CONTENT_URI_STEPS);
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case RECIPES:
                return RecipeEntry.CONTENT_LIST_TYPE_RECIPES;
            case RECIPES_WITH_ID:
                return RecipeEntry.CONTENT_ITEM_TYPE_RECIPES;
            case INGREDIENTS:
                return IngredientsEntry.CONTENT_LIST_TYPE_INGREDIENTS;
            case INGREDIENTS_WITH_ID:
                return IngredientsEntry.CONTENT_ITEM_TYPE_INGREDIENTS;
            case STEPS:
                return StepsEntry.CONTENT_LIST_TYPE_STEPS;
            case STEPS_WITH_ID:
                return StepsEntry.CONTENT_ITEM_TYPE_STEPS;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }
    }

    private Cursor queryItems(SQLiteDatabase db, String table, String[] projection, String selection,
                              String[] selectionArgs, String sortOrder) {
        return db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Uri insertItem(SQLiteDatabase db, String table, ContentValues contentValues, Uri contentUri) {
        long id = db.insert(table, null, contentValues);
        if (id > 0) {
            return ContentUris.withAppendedId(contentUri, id);
        } else {
            throw new android.database.SQLException("Failed to insert row");
        }
    }

    private int bulkInsertItems(SQLiteDatabase db, String table, ContentValues[] values, Uri uri) {
        db.beginTransaction();
        int rowsInserted = 0;
        try {
            for (ContentValues value : values) {
                long _id = db.insert(table, null, value);
                if (_id != -1) {
                    rowsInserted++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (rowsInserted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();
        return rowsInserted;
    }
}
