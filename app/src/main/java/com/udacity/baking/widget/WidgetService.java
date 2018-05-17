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

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.udacity.baking.model.Recipe;

import static com.udacity.baking.utilities.Constants.RECIPE_WIDGET_ACTION_UPDATE;
import static com.udacity.baking.utilities.Constants.RECIPE_WIDGET_DATA;

/**
 * Created by alex on 16/05/2018.
 */

public class WidgetService extends IntentService {

    public WidgetService() {
        super("WidgetService");
    }

    public static void startActionUpdateRecipeWidgets(Context context, Recipe recipe) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.setAction(RECIPE_WIDGET_ACTION_UPDATE);
        intent.putExtra(RECIPE_WIDGET_DATA, recipe);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (RECIPE_WIDGET_ACTION_UPDATE.equals(action) && intent.getParcelableExtra(RECIPE_WIDGET_DATA) != null) {
                handleActionUpdateWidgets(intent.getParcelableExtra(RECIPE_WIDGET_DATA));
            }
        }
    }

    private void handleActionUpdateWidgets(Recipe recipe) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, WidgetProvider.class));
        WidgetProvider.updateRecipeWidgets(this, appWidgetManager, recipe, appWidgetIds);
    }
}
