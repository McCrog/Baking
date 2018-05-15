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

package com.udacity.baking.viewmodel.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.udacity.baking.data.Repository;
import com.udacity.baking.model.Recipe;

/**
 * Created by McCrog on 09/04/2018.
 * {@link ViewModel} for {@link // TODO (2) Specify class name}
 */
public class DetailViewModel extends ViewModel {

    private final LiveData<Recipe> mRecipe;
    private final Repository mRepository;
    private final int mId;

    public DetailViewModel(Repository repository, int id) {
        mRepository = repository;
        mId = id;
        mRecipe = mRepository.getRecipe(mId);
    }

    public LiveData<Recipe> getRecipe() {
        return mRecipe;
    }
}
