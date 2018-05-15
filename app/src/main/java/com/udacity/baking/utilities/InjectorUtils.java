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

package com.udacity.baking.utilities;

import android.content.Context;

import com.udacity.baking.data.Repository;
import com.udacity.baking.data.database.DatabaseSource;
import com.udacity.baking.data.network.NetworkDataSource;
import com.udacity.baking.viewmodel.detail.DetailViewModelFactory;
import com.udacity.baking.viewmodel.list.RecipeViewModelFactory;

/**
 * Created by McCrog on 09/04/2018.
 *
 */

public class InjectorUtils {
    public static Repository provideRepository(Context context) {
        DatabaseSource database = DatabaseSource.getInstance(context.getApplicationContext());
        NetworkDataSource networkDataSource = NetworkDataSource.getInstance();
        return Repository.getInstance(database, networkDataSource);
    }

    public static DetailViewModelFactory provideDetailViewModelFactory(Context context, int id) {
        Repository repository = provideRepository(context.getApplicationContext());
        return new DetailViewModelFactory(repository, id);
    }

    public static RecipeViewModelFactory provideRecipeActivityViewModelFactory(Context context) {
        Repository repository = provideRepository(context.getApplicationContext());
        return new RecipeViewModelFactory(repository);
    }
}
