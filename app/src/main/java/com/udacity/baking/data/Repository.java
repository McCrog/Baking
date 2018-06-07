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

package com.udacity.baking.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.udacity.baking.AppExecutors;
import com.udacity.baking.data.database.DatabaseSource;
import com.udacity.baking.data.network.NetworkDataSource;
import com.udacity.baking.model.Recipe;

import java.util.List;

/**
 * Created by McCrog on 08/04/2018.
 *
 */

public class Repository {
    private static final String LOG_TAG = Repository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static Repository sInstance;

    private final DatabaseSource mDatabaseSource;
    private final NetworkDataSource mNetworkDataSource;
    private final AppExecutors mExecutors;

    private boolean mInitialized = false;

    private Repository(DatabaseSource databaseSource, NetworkDataSource networkDataSource,
                       AppExecutors executors) {
        mDatabaseSource = databaseSource;
        mNetworkDataSource = networkDataSource;
        mExecutors = executors;

        LiveData<List<Recipe>> networkData = mNetworkDataSource.getData();
        networkData.observeForever(newForecastsFromNetwork -> {
            mExecutors.diskIO().execute(() -> {
                deleteOldDatabaseData();
                mDatabaseSource.saveRecipes(newForecastsFromNetwork);
                startFetchDataFromDb();
            });
        });
    }

    public synchronized static Repository getInstance(DatabaseSource databaseSource,
                                                      NetworkDataSource networkDataSource,
                                                      AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new Repository(databaseSource, networkDataSource, executors);
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    private synchronized void initializeData() {
        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (mInitialized) return;
        mInitialized = true;

        mNetworkDataSource.scheduleRecurringFetchDataSync();

        if (isDataExist()) {
            mExecutors.diskIO().execute(this::startFetchDataFromDb);
        } else {
            startFetchNetworkService();
        }
    }

    public LiveData<List<Recipe>> getRecipes() {
        initializeData();
        return mDatabaseSource.getListData();
    }

    public LiveData<Recipe> getRecipe(int id) {
        initializeData();
        mExecutors.diskIO().execute(() -> {
            mDatabaseSource.loadItemData(id);
        });
        return mDatabaseSource.getItemData();
    }

    public void updateData() {
        mInitialized = false;
        initializeData();
    }

    public LiveData<Boolean> isError() {
        return mNetworkDataSource.isError();
    }

    private void startFetchNetworkService() {
        mNetworkDataSource.startFetchDataService();
    }

    private void startFetchDataFromDb() {
        mDatabaseSource.loadListData();
    }

    private void deleteOldDatabaseData() {
        mDatabaseSource.deleteRecipes();
    }

    private boolean isDataExist() {
        final boolean[] isExist = new boolean[1];
        mExecutors.diskIO().execute(() -> {
            isExist[0] = mDatabaseSource.isExist(1);
        });
        return isExist[0];
    }
}
