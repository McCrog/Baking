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

package com.udacity.baking.ui.list;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.udacity.baking.R;
import com.udacity.baking.ui.detail.RecipeDetailActivity;
import com.udacity.baking.utilities.InjectorUtils;
import com.udacity.baking.viewmodel.list.RecipeViewModel;
import com.udacity.baking.viewmodel.list.RecipeViewModelFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.baking.utilities.Constants.RECIPE_ID;

public class RecipeActivity extends AppCompatActivity
        implements RecipeAdapter.OnClickHandler {

    private static final String LOG_TAG = RecipeActivity.class.getSimpleName();

    @BindView(R.id.recipes_recycle_view)
    RecyclerView mRecyclerView;

    private RecipeAdapter mRecipeAdapter;
    private RecipeViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // For debug
        //Stetho.initializeWithDefaults(this);

        ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mRecipeAdapter = new RecipeAdapter(getApplicationContext(), this);
        mRecyclerView.setAdapter(mRecipeAdapter);

        initObserver();
    }

    @Override
    public void onClick(int index) {
        Log.d(LOG_TAG, "Click on item #" + index);

        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RECIPE_ID, mViewModel.getRecipes().getValue().get(index).getId());

        startActivity(intent);
    }

    private void initObserver() {
        RecipeViewModelFactory factory = InjectorUtils.provideRecipeActivityViewModelFactory(this.getApplicationContext());
        mViewModel = ViewModelProviders.of(this, factory).get(RecipeViewModel.class);

        mViewModel.getRecipes().observe(this, recipes -> {
            mRecipeAdapter.setData(recipes);
//            for (Recipe r : recipes) {
//                List<Ingredient> ingredients = r.getIngredients();
//                List<Step> steps = r.getSteps();
//
//                Log.e(LOG_TAG, "*******" + r.getId() + "\n"
//                + r.getName() + "\n"
//                + r.getServings() + "\n"
//                + r.getImage() + "\n");
//
//                for (Ingredient i : ingredients) {
//                    Log.e(LOG_TAG, "*******" + i.getQuantity() + "\n"
//                            + i.getMeasure() + "\n"
//                            + i.getIngredient() + "\n");
//                }
//
//                for (Step s : steps) {
//                    Log.e(LOG_TAG, "*******" + s.getId() + "\n"
//                            + s.getShortDescription() + "\n"
//                            + s.getDescription() + "\n"
//                            + s.getVideoURL() + "\n"
//                            + s.getThumbnailURL() + "\n");
//                }
//            }
        });
    }
}