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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.udacity.baking.R;
import com.udacity.baking.utilities.InjectorUtils;
import com.udacity.baking.viewmodel.detail.DetailViewModel;
import com.udacity.baking.viewmodel.detail.DetailViewModelFactory;

import static com.udacity.baking.utilities.Constants.ID_TAG;
import static com.udacity.baking.utilities.Constants.RECIPE_ID;
import static com.udacity.baking.utilities.Constants.STEP_TAG;

/**
 * Created by alex on 12/05/2018.
 */

public class RecipeDetailActivity extends AppCompatActivity implements MasterListStepAdapter.StepOnClickHandler {

    private static final String LOG_TAG = RecipeDetailActivity.class.getSimpleName();

    private DetailViewModel mViewModel;
    private DetailViewModelFactory mFactory;
//    private RecipeDetailMasterListFragment recipeDetailMasterListFragment;

    // Track whether to display a two-pane or single-pane UI
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private boolean mTwoPane;
    private int id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        id = intent.getIntExtra(RECIPE_ID, 0);

        if (savedInstanceState == null) {
            Bundle b = new Bundle();
            b.putInt(ID_TAG, id);

            RecipeDetailMasterListFragment fragment =
                    RecipeDetailMasterListFragment.newInstance(id);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_master_list_fragment, fragment)
                    .commit();
        } else {
            Log.d(LOG_TAG, "****************** savedInstanceState NOT NULL");
        }

        // Determine if you're creating a two-pane or single-pane display
        if (findViewById(R.id.step_constraint_layout) != null) {
            // This LinearLayout will only initially exist in the two-pane tablet case
            mTwoPane = true;
            // TODO (3) Add implementation for step info
        } else {
            // We're in single-pane mode and displaying fragments on a phone in separate activities
            mTwoPane = false;
        }
    }

    private void initObserver() {
        mFactory = InjectorUtils.provideDetailViewModelFactory(this.getApplicationContext(), id);
        mViewModel = ViewModelProviders.of(this, mFactory).get(DetailViewModel.class);

        mViewModel.getRecipe().observe(this, recipes -> {
            Log.d(LOG_TAG, recipes.getName());
        });
    }

    @Override
    public void onStepClick(int position) {
        Bundle b = new Bundle();
        b.putInt(STEP_TAG, position);
        b.putInt(RECIPE_ID, id);

        // Attach the Bundle to an intent
        final Intent intent = new Intent(this, RecipeDetailStepActivity.class);
        intent.putExtras(b);

        startActivity(intent);
    }
}
