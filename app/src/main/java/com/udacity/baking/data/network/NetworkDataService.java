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

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by McCrog on 24/02/2018.
 *
 */

class NetworkDataService {

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";

    private NetworkDataService() {
    }

    static NetworkDataApi getNetworkService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(NetworkDataApi.class);
    }

    private static OkHttpClient getOkHttpClient() {
        final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        return clientBuilder.build();
    }
}
