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
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.baking.R;

/**
 * Created by McCrog on 25/02/2018.
 *
 */

public class ImageUtils {
    private final static String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private final static String[] IMAGE_SIZE = {"w92", "w154", "w185", "w342", "w500", "w780", "original"};

    public static void loadImage(Context context, String posterPath, ImageView imageView, int imageSize) {
        Picasso.with(context)
                .load(BASE_IMAGE_URL + IMAGE_SIZE[imageSize] + posterPath)
                .placeholder(R.color.colorAccent)
                .into(imageView);
    }
}
