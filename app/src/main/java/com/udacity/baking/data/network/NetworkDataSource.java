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
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.udacity.baking.AppExecutors;
import com.udacity.baking.model.Recipe;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by McCrog on 23/02/2018.
 *
 */

public class NetworkDataSource {
    private static final String LOG_TAG = NetworkDataSource.class.getSimpleName();

    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;
    private static final String BAKING_SYNC_TAG = "baking-sync";

    private final MutableLiveData<List<Recipe>> mDownloadedData;
    private final MutableLiveData<Boolean> mIsError;
    private final NetworkDataApi mNetworkDataApi;
    private final Context mContext;
    private final AppExecutors mExecutors;

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static NetworkDataSource sInstance;

    private NetworkDataSource(Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;
        mDownloadedData = new MutableLiveData<>();
        mIsError = new MutableLiveData<>();
        mNetworkDataApi = NetworkDataService.getNetworkService();
    }

    /**
     * Get the singleton for this class
     */
    public static NetworkDataSource getInstance(Context context, AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new NetworkDataSource(context, executors);
                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }

    public void startFetchDataService() {
        Intent intentToFetch = new Intent(mContext, BakingSyncIntentService.class);
        mContext.startService(intentToFetch);
    }

    public void scheduleRecurringFetchDataSync() {
        Driver driver = new GooglePlayDriver(mContext);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Create the Job to periodically sync Baking
        Job syncSunshineJob = dispatcher.newJobBuilder()
                /* The Service that will be used to sync Baking's data */
                .setService(BakingFirebaseJobService.class)
                /* Set the UNIQUE tag used to identify this Job */
                .setTag(BAKING_SYNC_TAG)
                /*
                 * Network constraints on which this Job should run. We choose to run on any
                 * network, but you can also choose to run only on un-metered networks or when the
                 * device is charging. It might be a good idea to include a preference for this,
                 * as some users may not want to download any data on their mobile plan. ($$$)
                 */
                .setConstraints(Constraint.ON_ANY_NETWORK)
                /*
                 * setLifetime sets how long this job should persist. The options are to keep the
                 * Job "forever" or to have it die the next time the device boots up.
                 */
                .setLifetime(Lifetime.FOREVER)
                /*
                 * We want Baking's data to stay up to date, so we tell this Job to recur.
                 */
                .setRecurring(true)
                /*
                 * We want the data to be synced every 3 to 4 hours. The first argument for
                 * Trigger's static executionWindow method is the start of the time frame when the
                 * sync should be performed. The second argument is the latest point in time at
                 * which the data should be synced. Please note that this end time is not
                 * guaranteed, but is more of a guideline for FirebaseJobDispatcher to go off of.
                 */
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                /*
                 * If a Job with the tag with provided already exists, this new job will replace
                 * the old one.
                 */
                .setReplaceCurrent(true)
                /* Once the Job is ready, call the builder's build method to return the Job */
                .build();

        // Schedule the Job with the dispatcher
        dispatcher.schedule(syncSunshineJob);
    }

    public void loadListData() {
        mExecutors.networkIO().execute(() -> {
            try {
                Call<List<Recipe>> recipesCall = mNetworkDataApi.getRecipes();

                recipesCall.enqueue(new Callback<List<Recipe>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            mDownloadedData.postValue(response.body());
                            mIsError.postValue(false);
                        }
                    }
        
                    @Override
                    public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                        Log.e(LOG_TAG, t.toString());
                        mIsError.postValue(true);
                    }
                });
            } catch (Exception e) {
                // Server probably invalid
                e.printStackTrace();
            }
        });
    }

    public LiveData<List<Recipe>> getData() {
        return mDownloadedData;
    }

    public LiveData<Boolean> isError() {
        return mIsError;
    }
}
