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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.baking.R;
import com.udacity.baking.model.Recipe;
import com.udacity.baking.utilities.InjectorUtils;
import com.udacity.baking.viewmodel.detail.DetailViewModel;
import com.udacity.baking.viewmodel.detail.DetailViewModelFactory;
import com.udacity.baking.widget.CollectionAppWidgetProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.udacity.baking.utilities.Constants.ID_TAG;

/**
 * Created by alex on 12/05/2018.
 */

public class RecipeDetailMasterListFragment extends Fragment {

    @BindView(R.id.ingredients_recycle_view)
    RecyclerView mIngredientsRecyclerView;
    @BindView(R.id.steps_recycle_view)
    RecyclerView mStepsRecyclerView;

    private int mId = 0;
    private Recipe mRecipe;
    
    private MasterListIngredientAdapter mIngredientAdapter;
    private MasterListStepAdapter mStepAdapter;

    // Mandatory empty constructor
    public RecipeDetailMasterListFragment() {
    }

    public static RecipeDetailMasterListFragment newInstance (int id) {
        RecipeDetailMasterListFragment fragment = new RecipeDetailMasterListFragment();
        // Set the bundle arguments for the fragment.
        Bundle arguments = new Bundle();
        arguments.putInt(ID_TAG, id);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(ID_TAG)) {
                mId = bundle.getInt(ID_TAG);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail_master_list, container, false);

        ButterKnife.bind(this, rootView);

        mIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mStepsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mIngredientAdapter = new MasterListIngredientAdapter();
        mStepAdapter = new MasterListStepAdapter((RecipeDetailActivity) getActivity());

        mIngredientsRecyclerView.setAdapter(mIngredientAdapter);
        mStepsRecyclerView.setAdapter(mStepAdapter);
        mIngredientsRecyclerView.setNestedScrollingEnabled(false);
        mIngredientsRecyclerView.setNestedScrollingEnabled(false);

        initObserver();

        // Return the root view
        return rootView;
    }

    @OnClick(R.id.add_to_widget_buton)
    public void addRecipetoWidget() {
        CollectionAppWidgetProvider.sendRefreshBroadcast(getContext());
        InjectorUtils.provideAppPreferences(getContext()).saveWidgetPreference(mId);
//        InjectorUtils.provideAppPreferences(getContext()).saveRecipeName(mRecipe.getName());
    }

    private void initObserver() {
        DetailViewModelFactory mFactory = InjectorUtils.provideDetailViewModelFactory(this.getContext(), mId);
        DetailViewModel mViewModel = ViewModelProviders.of(this, mFactory).get(DetailViewModel.class);

        mViewModel.getRecipe().observe(this, recipe -> {
            mRecipe = recipe;
            setData(recipe);
        });
    }

    private void setData(Recipe recipe) {
        mIngredientAdapter.setData(recipe.getIngredients());
        mStepAdapter.setData(recipe.getSteps());
    }
}
