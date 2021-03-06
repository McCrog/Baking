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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.udacity.baking.R;
import com.udacity.baking.ui.detail.RecipeDetailActivity;
import com.udacity.baking.ui.list.RecipeActivity;
import com.udacity.baking.utilities.InjectorUtils;

/**
 * Created by McCrog on 20/05/2018.
 *
 */

public class CollectionAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = updateWidgetListView(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, CollectionAppWidgetProvider.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(
                    appWidgetManager.getAppWidgetIds(componentName),
                    R.id.ll_recipe_widget_ingredient_list_view);
            onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(componentName));
        }
        super.onReceive(context, intent);
    }

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, CollectionAppWidgetProvider.class));
        context.sendBroadcast(intent);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_layout);

        Intent intent = new Intent(context, IngredientWidgetRemoteViewService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        String recipeName = InjectorUtils.provideAppPreferences(context).getRecipeName();
        if (!recipeName.matches("")) {
            views.setTextViewText(R.id.recipe_widget_title, recipeName);

            Intent detailIntent = RecipeDetailActivity.getDetailIntent(context,
                    InjectorUtils.provideAppPreferences(context).getWidgetPreference());
            PendingIntent titlePendingIntent = PendingIntent.getActivity(context, 0, detailIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.recipe_widget_title, titlePendingIntent);
        } else {
            Intent mainIntent = new Intent(context, RecipeActivity.class);
            PendingIntent titlePendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
            views.setOnClickPendingIntent(R.id.recipe_widget_title, titlePendingIntent);
        }

        views.setRemoteAdapter(R.id.ll_recipe_widget_ingredient_list_view, intent);
        views.setEmptyView(R.id.ll_recipe_widget_ingredient_list_view, R.id.empty_view);

        return views;
    }
}
