/*
 * Copyright (C) 2018. The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.udacity.baking.widget;

import android.content.Context;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.baking.R;
import com.udacity.baking.data.database.DatabaseContract;
import com.udacity.baking.utilities.InjectorUtils;

import static com.udacity.baking.data.database.DatabaseContract.IngredientsEntry.INGREDIENTS_INGREDIENT;
import static com.udacity.baking.data.database.DatabaseContract.IngredientsEntry.INGREDIENTS_MEASURE;
import static com.udacity.baking.data.database.DatabaseContract.IngredientsEntry.INGREDIENTS_QUANTITY;

/**
 * Created by McCrog on 20/05/2018.
 *
 */

public class IngredientWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Cursor mCursor;

    public IngredientWidgetRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        int id = InjectorUtils.provideAppPreferences(mContext).getWidgetPreference();
        String idValue = String.valueOf(id);

        if (mCursor != null) mCursor.close();

        String selectionClause = DatabaseContract.IngredientsEntry.RECIPE_ID_KEY + " =?";
        String[] selectionArgs = {idValue};
        mCursor = mContext.getContentResolver().query(
                DatabaseContract.IngredientsEntry.CONTENT_URI_INGREDIENTS,
                null,
                selectionClause,
                selectionArgs,
                null
        );
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) mCursor.close();
    }

    @Override
    public int getCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) return null;

        mCursor.moveToPosition(position);

        int quantityIndex = mCursor.getColumnIndex(INGREDIENTS_QUANTITY);
        int measureIndex = mCursor.getColumnIndex(INGREDIENTS_MEASURE);
        int ingredientIndex = mCursor.getColumnIndex(INGREDIENTS_INGREDIENT);

        Double quantity = mCursor.getDouble(quantityIndex);
        String measure = mCursor.getString(measureIndex);
        String ingredient = mCursor.getString(ingredientIndex);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

        views.setTextViewText(R.id.recipe_widget_ingredient_item_tv, ingredient);
        views.setTextViewText(R.id.recipe_widget_quantity_item_tv, String.valueOf(quantity));
        views.setTextViewText(R.id.recipe_widget_measure_item_tv, measure);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
