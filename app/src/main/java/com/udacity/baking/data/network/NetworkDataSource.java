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

package com.udacity.baking.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.udacity.baking.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by McCrog on 23/02/2018.
 *
 */

public class NetworkDataSource {
    private static final String LOG_TAG = NetworkDataSource.class.getSimpleName();

    private final MutableLiveData<List<Recipe>> downloadedData;
    private final NetworkDataApi mNetworkDataApi = NetworkDataService.getNetworkService();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static NetworkDataSource sInstance;

    private NetworkDataSource() {
        downloadedData = new MutableLiveData<>();
    }

    /**
     * Get the singleton for this class
     */
    public static NetworkDataSource getInstance() {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new NetworkDataSource();
                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }

    public void fetchRecipes() {
        Call<List<Recipe>> recipesCall = mNetworkDataApi.getRecipes();

        recipesCall.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    downloadedData.postValue(response.body());

//                    List<Recipe> tmp = response.body();
//                    for (Recipe r : tmp) {
//                        Log.d(LOG_TAG, "" + r.getName());
//                        Log.d(LOG_TAG, "" + r.getIngredients().size());
//                        Log.d(LOG_TAG, "" + r.getIngredients().get(0).getIngredient());
//                        Log.d(LOG_TAG, "" + r.getSteps().size());
//                        Log.d(LOG_TAG, "" + r.getSteps().get(0).getDescription());
//                    }
//                    Log.d(LOG_TAG, "" + response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                Log.e(LOG_TAG, t.toString());
            }
        });
    }

    public LiveData<List<Recipe>> getData() {
        return downloadedData;
    }
}
