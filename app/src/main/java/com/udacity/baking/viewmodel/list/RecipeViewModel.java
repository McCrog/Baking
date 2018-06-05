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

package com.udacity.baking.viewmodel.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.udacity.baking.data.Repository;
import com.udacity.baking.model.Recipe;
import com.udacity.baking.ui.list.RecipeActivity;

import java.util.List;

/**
 * Created by McCrog on 09/04/2018.
 * {@link ViewModel} for {@link RecipeActivity}
 */
public class RecipeViewModel extends ViewModel {

    private final LiveData<List<Recipe>> mRecipe;
    private final Repository mRepository;

    public RecipeViewModel(Repository repository) {
        mRepository = repository;
        mRecipe = mRepository.getRecipes();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipe;
    }

    public void updateData() {
        mRepository.updateData();
    }

    public LiveData<Boolean> checkError() {
        return mRepository.isError();
    }
}
