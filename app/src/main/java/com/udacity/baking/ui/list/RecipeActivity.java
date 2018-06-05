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
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.udacity.baking.R;
import com.udacity.baking.ui.detail.RecipeDetailActivity;
import com.udacity.baking.utilities.InjectorUtils;
import com.udacity.baking.viewmodel.list.RecipeViewModel;
import com.udacity.baking.viewmodel.list.RecipeViewModelFactory;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.baking.utilities.Constants.RECIPE_ID;

public class RecipeActivity extends AppCompatActivity
        implements RecipeAdapter.OnClickHandler, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recipes_recycle_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindInt(R.integer.recipe_list_columns)
    int mNumberOfColumns;

    private RecipeAdapter mRecipeAdapter;
    private RecipeViewModel mViewModel;
    private Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // For debug
        //Stetho.initializeWithDefaults(this);

        ButterKnife.bind(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, mNumberOfColumns);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecipeAdapter = new RecipeAdapter(getApplicationContext(), this);
        mRecyclerView.setAdapter(mRecipeAdapter);

        initObserver();
    }

    @Override
    public void onClick(int index) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RECIPE_ID, mViewModel.getRecipes().getValue().get(index).getId());

        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);

        mViewModel.updateData();

        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }

    private void initObserver() {
        RecipeViewModelFactory factory = InjectorUtils.provideRecipeActivityViewModelFactory(this.getApplicationContext());
        mViewModel = ViewModelProviders.of(this, factory).get(RecipeViewModel.class);

        mViewModel.getRecipes().observe(this, recipes -> {
            mRecipeAdapter.setData(recipes);
        });

        mViewModel.checkError().observe(this, error -> {
            if (error) {
                mSnackbar = Snackbar.make(findViewById(R.id.refresh), R.string.network_error, Snackbar.LENGTH_INDEFINITE);
                mSnackbar.setAction(R.string.retry, snackbarOnClickListener);
                mSnackbar.setActionTextColor(getResources().getColor(R.color.lightRed));
                mSnackbar.show();
            }
        });
    }

    View.OnClickListener snackbarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mSnackbar.dismiss();
            mViewModel.updateData();
        }
    };
}
