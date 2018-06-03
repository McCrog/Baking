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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.baking.R;
import com.udacity.baking.model.Recipe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by alex on 10/05/2018.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final List<Recipe> mRecipe;
    private final Context mContext;
    private final OnClickHandler mClickHandler;

    /**
     * The interface that receives onStepClick messages.
     */
    public interface OnClickHandler {
        void onClick(int index);
    }

    public RecipeAdapter(Context context, OnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;
        mRecipe = new ArrayList<>();
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = mRecipe.get(position);

        holder.mName.setText(recipe.getName());
        holder.mIngredients.setText(mContext.getString(R.string.ingredients_format, recipe.getIngredients().size()));
        holder.mSteps.setText(mContext.getString(R.string.steps_format, recipe.getSteps().size()));
        holder.mServings.setText(String.valueOf(recipe.getServings()));
    }

    @Override
    public int getItemCount() {
        return mRecipe.size();
    }

    public void setData(List<Recipe> newRecipes) {
        mRecipe.clear();
        mRecipe.addAll(newRecipes);
        notifyDataSetChanged();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_card_view_name_tv)
        TextView mName;
        @BindView(R.id.recipe_card_view_ingredients_tv)
        TextView mIngredients;
        @BindView(R.id.recipe_card_view_steps_tv)
        TextView mSteps;
        @BindView(R.id.recipe_card_view_servings_tv)
        TextView mServings;

        public RecipeViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);
        }
    }
}
