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

package com.udacity.baking.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.udacity.baking.R;

import static com.udacity.baking.utilities.Constants.RECIPE_ID;
import static com.udacity.baking.utilities.Constants.STEP_TAG;

/**
 * Created by alex on 13/05/2018.
 */

public class RecipeDetailStepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        Intent intent = getIntent();
        int id = intent.getIntExtra(RECIPE_ID, 0);
        int stepIndex = intent.getIntExtra(STEP_TAG, 0);

        if (savedInstanceState == null) {
            Bundle b = new Bundle();
            b.putInt(RECIPE_ID, id);
            b.putInt(STEP_TAG, stepIndex);

            RecipeDetailStepFragment stepFragment = RecipeDetailStepFragment.newInstance(id, stepIndex);
            // Add the fragment to its container using a FragmentManager and a Transaction
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.step_container, stepFragment)
                    .commit();
        }

        boolean mIsTablet = getResources().getBoolean(R.bool.isTablet);

        if (!mIsTablet) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
