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

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.udacity.baking.R;
import com.udacity.baking.utilities.InjectorUtils;
import com.udacity.baking.viewmodel.detail.DetailViewModel;
import com.udacity.baking.viewmodel.detail.DetailViewModelFactory;

import static com.udacity.baking.utilities.Constants.RECIPE_ID;
import static com.udacity.baking.utilities.Constants.STEP_TAG;

/**
 * Created by alex on 12/05/2018.
 */

public class RecipeDetailActivity extends AppCompatActivity implements MasterListStepAdapter.StepOnClickHandler {

    // Track whether to display a two-pane or single-pane UI
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private boolean mIsTablet;
    private int mId = 0;
    private int mStepIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        mId = intent.getIntExtra(RECIPE_ID, 0);
        mIsTablet = getResources().getBoolean(R.bool.isTablet);

        initObserver();

        if (savedInstanceState == null) {
            Bundle b = new Bundle();
            b.putInt(RECIPE_ID, mId);

            RecipeDetailMasterListFragment fragment =
                    RecipeDetailMasterListFragment.newInstance(mId);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_master_list_fragment, fragment)
                    .commit();

            // Determine if you're creating a two-pane or single-pane display
            if (mIsTablet) {
                RecipeDetailStepFragment stepFragment = RecipeDetailStepFragment.newInstance(mId, mStepIndex);
                // Add the fragment to its container using a FragmentManager and a Transaction
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.step_container, stepFragment)
                        .commit();

            }
        }
    }

    public static Intent getDetailIntent(Context packageContext, int id) {
        Intent intent = new Intent(packageContext, RecipeDetailActivity.class);
        intent.putExtra(RECIPE_ID, id);
        return intent;
    }

    @Override
    public void onStepClick(int position) {
        if (mIsTablet) {
            mStepIndex = position;
            RecipeDetailStepFragment stepFragment = RecipeDetailStepFragment.newInstance(mId, mStepIndex);
            // Add the fragment to its container using a FragmentManager and a Transaction
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.step_container, stepFragment)
                    .commit();
        } else {
            Bundle b = new Bundle();
            b.putInt(STEP_TAG, position);
            b.putInt(RECIPE_ID, mId);

            // Attach the Bundle to an intent
            final Intent intent = new Intent(this, RecipeDetailStepActivity.class);
            intent.putExtras(b);

            startActivity(intent);
        }
    }

    private void initObserver() {
        DetailViewModelFactory mFactory = InjectorUtils.provideDetailViewModelFactory(getApplicationContext(), mId);
        DetailViewModel mViewModel = ViewModelProviders.of(this, mFactory).get(DetailViewModel.class);

        mViewModel.getRecipe().observe(this, recipe -> {
            getSupportActionBar().setTitle(recipe.getName());
        });
    }
}
